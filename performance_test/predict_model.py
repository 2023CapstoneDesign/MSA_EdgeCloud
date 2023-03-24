import sys

if len(sys.argv) != 2:
    raise Exception("usage: predict.py IMAGE_PATH")

import numpy as np
import cv2
import torch
import timm

device = torch.device("cpu")
model = timm.create_model("mobilenetv3_large_100", pretrained=False)

# pytorch_total_params = sum(p.numel() for p in model.parameters())

model.eval()
model.load_state_dict(torch.load(f"./model/model_best.pt", map_location=device))

image = np.array(cv2.imread(f'{sys.argv[1]}'))
image = cv2.resize(image, dsize=(224, 224),interpolation=cv2.INTER_LINEAR)
image_swap = np.swapaxes(image, 0,2)
image_swap = np.expand_dims(image_swap, axis=0)

tensor = torch.from_numpy(image_swap).type(torch.FloatTensor)
output = model(tensor)
_, pred = torch.max(output, dim=1)

print(pred)