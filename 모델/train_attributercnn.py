import os
import math
import argparse
import sys

import logging
from tqdm import tqdm
import torch
import torch.nn as nn
import torch.distributed as dist
from torchvision import transforms

import utils
import transform as T
from torch.utils.tensorboard import SummaryWriter
from models.attributercnn import matchrcnn_resnet50_fpn
from dataset1 import load_data, CLOTHING_CATEGORIES, MATERIAL_CATEGORIES, SLEEVE_CATEGORIES, SHIRT_SLEEVES, NECKLINE_CATEGORIES, COLLAR_CATEGORIES, FIT_CATEGORIES

gpu_map = [0]

pil_to_tensor = transforms.ToTensor()

def get_transform(train):
    select_transforms = []
    select_transforms.append(T.ToTensor())
    if train:
        select_transforms.append(T.RandomHorizontalFlip(0.5))
    select_transforms += [
        transforms.Normalize((0.485, 0.456, 0.406), (0.229, 0.224, 0.225)),
    ]
    return T.Compose(select_transforms)



logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

def train_one_epoch_matchrcnn(model, optimizer, data_loader, device, epoch, print_freq, writer=None):
    if 'WORLD_SIZE' in os.environ and int(os.environ['WORLD_SIZE']) > 1:
        rank = dist.get_rank()
    else:
        rank = 0
    model.train()
    metric_logger = utils.MetricLogger(delimiter="  ")
    metric_logger.add_meter('lr', utils.SmoothedValue(window_size=1, fmt='{value:.6f}'))
    header = 'Epoch: [{}]'.format(epoch)

    lr_scheduler = None
    if epoch == 0:
        warmup_factor = 1. / 1000
        warmup_iters = min(1000, len(data_loader) - 1)
        lr_scheduler = utils.warmup_lr_scheduler(optimizer, warmup_iters, warmup_factor)

    count = -1
    
    for i, (images, targets, idxs) in enumerate(metric_logger.log_every(data_loader, print_freq, header, rank=rank)):
        count += 1
        images = list(image.to(device) for image in images)

        new_targets = []
        for t in targets:
            new_target = {k: torch.tensor(v).to(device) if k != "attributes" else {} for k, v in t.items()}

            if "masks" not in new_target:
                height, width = new_target["boxes"].shape[0], new_target["boxes"].shape[1]
                new_target["masks"] = torch.zeros((len(new_target["boxes"]), height, width), dtype=torch.uint8).to(device)

            for key, value in t["attributes"].items():
                new_target["attributes"][key] = value.to(device)

            new_targets.append(new_target)

        targets = new_targets

        loss_dict = model(images, targets)
        losses = sum(loss for loss in loss_dict.values())

        loss_dict_reduced = utils.reduce_dict(loss_dict)
        losses_reduced = sum(loss for loss in loss_dict_reduced.values())

        if writer is not None and (((count % print_freq) == 0) or count == 0):
            global_step = (epoch * len(data_loader)) + count
            for k, v in loss_dict_reduced.items():
                writer.add_scalar(k, v.item(), global_step=global_step)
            writer.add_scalar("loss", losses.item(), global_step=global_step)

        loss_value = losses_reduced.item()

        if not math.isfinite(loss_value):
            logging.error("Loss is {}, stopping training".format(loss_value))
            logging.error(loss_dict_reduced)
            logging.error(idxs)
            sys.exit(1)

        optimizer.zero_grad()
        losses.backward()
        optimizer.step()

        if lr_scheduler is not None:
            lr_scheduler.step()

        # tqdm 진행 바에 현재 손실 값을 표시
        tqdm.write(f"Batch {i}: Loss: {loss_value:.4f}")
        metric_logger.update(loss=losses_reduced, **loss_dict_reduced)
        metric_logger.update(lr=optimizer.param_groups[0]["lr"])

    logging.info("Epoch finished by process #%d" % rank)




def train(args):
    #GPU 설정------------------------------------------------------------------------------------------------------------
    os.environ['CUDA_VISIBLE_DEVICES'] = args.gpus
    if 'WORLD_SIZE' in os.environ:
        distributed = int(os.environ['WORLD_SIZE']) > 1
        rank = args.local_rank
        print("Distributed training with %d processors. This is #%s"
              % (int(os.environ['WORLD_SIZE']), rank))
    else:
        distributed = False
        rank = 0
        print("GPU 없음!")

    if distributed:
        os.environ['NCCL_BLOCKING_WAIT'] = "1"
        torch.cuda.set_device(gpu_map[rank])
        torch.distributed.init_process_group(backend='nccl', init_method='env://')
        device = torch.device(torch.cuda.current_device())
    else:
        device = torch.device("cuda")
    
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    #GPU 설정 끝 ------------------------------------------------------------------------------------------------------------

    data_loader = load_data(train=args.train_mode, batch_size=16, num_workers=8, data_root="./data/")

    # MODEL ------------------------------------------------------------------------------------------------------------
    from models.attributercnn import params as c_params
    model = matchrcnn_resnet50_fpn(
        pretrained=False,
        num_classes=len(CLOTHING_CATEGORIES),
        **c_params
    )
    
    root_path = "/".join(__file__.split("/")[:-1])
    
    pretrained_path = "ckpt/df2matchrcnn"
    pretrained_path = os.path.join(root_path, pretrained_path)

    if os.path.exists(pretrained_path):
        savefile = torch.load(pretrained_path)
        sd = savefile['model_state_dict']
        sd = {".".join(k.split(".")[1:]): v for k, v in sd.items()}
        model.load_saved_matchrcnn(sd, new_num_classes=len(CLOTHING_CATEGORIES), attribute_dict={
            "material": ("multi", len(MATERIAL_CATEGORIES) + 1),
            "sleeve": ("single", len(SLEEVE_CATEGORIES) + 1),
            "shirt_sleeve": ("single", len(SHIRT_SLEEVES) + 1),
            "neckline": ("single", len(NECKLINE_CATEGORIES) + 1),
            "collar": ("single", len(COLLAR_CATEGORIES) + 1),
            "fit": ("single", len(FIT_CATEGORIES) + 1)
        })
        print(">>>Loaded pretrained weights.")
    else:
        print(">>>No pretrained weights found, initializing model with random weights.")
    
    model.to(device)

    params = [p for p in model.parameters() if p.requires_grad]
    optimizer = torch.optim.SGD(params, lr=args.learning_rate,
                                momentum=0.9)

    lr_scheduler = torch.optim.lr_scheduler.MultiStepLR(optimizer, milestones=args.milestones)

    if rank == 0:
        writer = SummaryWriter(os.path.join(args.save_path, args.save_tag))
    else:
        writer = None

    for epoch in range(args.num_epochs):
        # train for one epoch, printing every 10 iterations
        print(">>>Starting epoch %d for process %d" % (epoch, rank))
        train_one_epoch_matchrcnn(model, optimizer, data_loader, device, epoch, args.print_freq, writer)
        # update the learning rate
        lr_scheduler.step()

        if rank == 0 and epoch != 0 and epoch % args.save_epochs == 0:
            os.makedirs(args.save_path, exist_ok=True)
            torch.save({
                'epoch': epoch,
                'model_state_dict': model.state_dict(),
                'optimizer_state_dict': optimizer.state_dict(),
                'scheduler_state_dict': lr_scheduler.state_dict(),
            }, os.path.join(args.save_path, (args.save_tag + "_epoch%03d") % epoch))

    os.makedirs(args.save_path, exist_ok=True)
    torch.save({
            'epoch': args.num_epochs,
            'model_state_dict': model.state_dict(),
            'optimizer_state_dict': optimizer.state_dict(),
            'scheduler_state_dict': lr_scheduler.state_dict(),
    }, os.path.join(args.save_path, "final_model"))

    print("That's it!")


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description="Match R-CNN Training")
    parser.add_argument("--local_rank", type=int, default=0)
    parser.add_argument("--gpus", type=str, default="0")
    parser.add_argument("--n_workers", type=int, default=1)

    parser.add_argument("--batch_size", type=int, default=20)
    parser.add_argument("--root_train", type=str, default='data/deepfashion2/validation/image')
    parser.add_argument("--train_annots", type=str, default='data/deepfashion2/validation/annots.json')

    parser.add_argument("--num_epochs", type=int, default=12)
    parser.add_argument("--milestones", type=int, default=[6, 9])
    parser.add_argument("--learning_rate", type=float, default=0.02)

    parser.add_argument("--print_freq", type=int, default=100)
    parser.add_argument("--save_epochs", type=int, default=1)

    parser.add_argument('--save_path', type=str, default="ckpt2/attributernn")
    parser.add_argument('--save_tag', type=str, default="attributernn")

    parser.add_argument("--train_mode", action='store_true', help="Set this flag to enable training mode")

    
    args = parser.parse_args()

    train(args)

