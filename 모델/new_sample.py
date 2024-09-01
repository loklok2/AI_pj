from dataset import load_data
    
dataloader = load_data(train=False, batch_size=2, num_workers=0, data_root="./data")
for batch in dataloader:
    images, targets, idxs = batch

    for idx, (image, target) in enumerate(zip(images, targets)):
        # print("box", target["boxes"][0])
        saveimg_bbox(image, f"examples/{idx}_bboxed.jpg", target["boxes"][0])
    print(batch)