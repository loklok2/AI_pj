import os
import time
import datetime
from collections import defaultdict, deque

import torch
import torch.distributed as dist
import torch.distributed as dist

def get_world_size():
    if not dist.is_available():
        return 1
    if not dist.is_initialized():
        return 1
    return dist.get_world_size()

class SmoothedValue(object):
    def __init__(self, window_size=20, fmt=None):
        if fmt is None:
            fmt = "{median:.4f} ({global_avg:.4f})"
        self.deque = deque(maxlen=window_size)
        self.total = 0.0
        self.count = 0
        self.fmt = fmt

    def update(self, value, n=1):
        self.deque.append(value)
        self.count += n
        self.total += value * n

    def synchronize_between_processes(self):
        """
        Warning: does not synchronize the deque!
        """
        if not is_dist_avail_and_initialized():
            return
        t = torch.tensor([self.count, self.total], dtype=torch.float64, device='cuda')
        dist.barrier()
        dist.all_reduce(t)
        t = t.tolist()
        self.count = int(t[0])
        self.total = t[1]

    @property
    def median(self):
        d = torch.tensor(list(self.deque))
        return d.median().item()

    @property
    def avg(self):
        d = torch.tensor(list(self.deque), dtype=torch.float32)
        return d.mean().item()

    @property
    def global_avg(self):
        return self.total / self.count

    @property
    def max(self):
        return max(self.deque)

    @property
    def value(self):
        return self.deque[-1]

    def __str__(self):
        return self.fmt.format(
            median=self.median,
            avg=self.avg,
            global_avg=self.global_avg,
            max=self.max,
            value=self.value)

def reduce_dict(input_dict, average=True):
    """
    Args:
        input_dict (dict): 모든 프로세스에서 값을 합산하거나 평균할 딕셔너리.
        average (bool): 합산된 값을 평균으로 나눌지 여부. True이면 평균, False이면 합산된 값 반환.
    
    모든 프로세스의 딕셔너리 값을 합산하거나 평균하여 각 프로세스가 동일한 결과를 가지도록 합니다.
    입력으로 받은 input_dict과 동일한 구조의 딕셔너리를 반환하며, 값은 합산 또는 평균된 결과입니다.
    """
    world_size = get_world_size()  # 현재의 world_size를 가져옵니다.
    if world_size < 2:  # 만약 world_size가 1이라면(즉, 분산 학습이 아니면)
        return input_dict  # 그냥 input_dict을 그대로 반환합니다.
    
    with torch.no_grad():  # 그래디언트 계산을 하지 않도록 no_grad 문맥을 사용합니다.
        names = []  # 키를 저장할 리스트
        values = []  # 값을 저장할 리스트
        # 키를 정렬된 순서로 정리하여 일관성을 유지합니다.
        for k in sorted(input_dict.keys()):
            names.append(k)  # 정렬된 키를 names에 추가
            values.append(input_dict[k])  # 정렬된 키에 해당하는 값을 values에 추가
        
        values = torch.stack(values, dim=0)  # 값들을 스택으로 쌓아 하나의 텐서로 만듭니다.
        dist.all_reduce(values)  # 모든 프로세스에서 값을 합산합니다.
        
        if average:  # 만약 평균을 내야 한다면
            values /= world_size  # world_size로 값을 나누어 평균을 계산합니다.
        
        # 합산 또는 평균된 값을 원래의 키에 매핑하여 딕셔너리로 반환합니다.
        reduced_dict = {k: v for k, v in zip(names, values)}
    
    return reduced_dict  # 합산 또는 평균된 결과를 반환합니다.


class MetricLogger(object):
    def __init__(self, delimiter="\t"):
        self.meters = defaultdict(SmoothedValue)
        self.delimiter = delimiter

    def update(self, **kwargs):
        for k, v in kwargs.items():
            if isinstance(v, torch.Tensor):
                v = v.item()
            assert isinstance(v, (float, int))
            self.meters[k].update(v)

    def __getattr__(self, attr):
        if attr in self.meters:
            return self.meters[attr]
        if attr in self.__dict__:
            return self.__dict__[attr]
        raise AttributeError("'{}' object has no attribute '{}'".format(
            type(self).__name__, attr))

    def __str__(self):
        loss_str = []
        for name, meter in self.meters.items():
            loss_str.append(
                "{}: {}".format(name, str(meter))
            )
        return self.delimiter.join(loss_str)

    def synchronize_between_processes(self):
        for meter in self.meters.values():
            meter.synchronize_between_processes()

    def add_meter(self, name, meter):
        self.meters[name] = meter

    def log_every(self, iterable, print_freq, header=None, rank=0):
        i = 0
        if not header:
            header = ''
        start_time = time.time()
        end = time.time()
        iter_time = SmoothedValue(fmt='{avg:.4f}')
        data_time = SmoothedValue(fmt='{avg:.4f}')
        space_fmt = ':' + str(len(str(len(iterable)))) + 'd'
        if torch.cuda.is_available():
            log_msg = self.delimiter.join([
                header,
                '[{0' + space_fmt + '}/{1}]',
                'eta: {eta}',
                '{meters}',
                'time: {time}',
                'data: {data}',
                'max mem: {memory:.0f}'
            ])
        else:
            log_msg = self.delimiter.join([
                header,
                '[{0' + space_fmt + '}/{1}]',
                'eta: {eta}',
                '{meters}',
                'time: {time}',
                'data: {data}'
            ])
        MB = 1024.0 * 1024.0
        for obj in iterable:
            data_time.update(time.time() - end)
            yield obj
            iter_time.update(time.time() - end)
            if i % print_freq == 0 or i == len(iterable) - 1:
                eta_seconds = iter_time.global_avg * (len(iterable) - i)
                eta_string = str(datetime.timedelta(seconds=int(eta_seconds)))
                if rank == 0:
                    if torch.cuda.is_available():
                        print(log_msg.format(
                            i, len(iterable), eta=eta_string,
                            meters=str(self),
                            time=str(iter_time), data=str(data_time),
                            memory=torch.cuda.max_memory_allocated() / MB))
                    else:
                        print(log_msg.format(
                            i, len(iterable), eta=eta_string,
                            meters=str(self),
                            time=str(iter_time), data=str(data_time)))
            i += 1
            end = time.time()
        total_time = time.time() - start_time
        total_time_str = str(datetime.timedelta(seconds=int(total_time)))
        print('{} Total time: {} ({:.4f} s / it)'.format(
            header, total_time_str, total_time / len(iterable)))

def warmup_lr_scheduler(optimizer, warmup_iters, warmup_factor):
    def f(x):
        if x >= warmup_iters:
            return 1
        alpha = float(x) / warmup_iters
        return warmup_factor * (1 - alpha) + alpha

    return torch.optim.lr_scheduler.LambdaLR(optimizer, f)


