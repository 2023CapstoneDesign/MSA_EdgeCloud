# if len(sys.argv) != 2:
#     raise Exception("usage: predict.py IMAGE_PATH")

import time
import os

import numpy as np
import cv2
import torch
import timm
from http import server
import socketserver
import cgi
import io

start = time.time()

birds = []
with open("./birds.txt", "r") as f:
    birds = f.read().split("\n")

device = torch.device("cpu")
model = timm.create_model("mobilenetv3_large_100", pretrained=False, num_classes=400)

model.eval()
model.load_state_dict(torch.load(f"./model/model_best.pt", map_location=device))

# PORT = 30080

PORT = int(os.environ.get("TORCH_PORT", 30080))

class myHandler(server.BaseHTTPRequestHandler):
    def do_POST(self):
        r, info = self.make_post_data()

        self.send_response(200)
        self.send_header("Content-Type", "text/plain")
        self.end_headers()
        if r:
            self.wfile.write(bytes(str(info), "utf-8"))
        else:
            self.wfile.write(bytes(f"Failed, err: {info}", "utf-8"))
            

    def make_post_data(self):
        try:
            ctype, pdict = cgi.parse_header(self.headers['Content-Type'])
        except Exception as e:
            return False, "Please make form-data and put image(png, jpg) as \"img\"."
        
        pdict['boundary'] = bytes(pdict['boundary'], "utf-8")
        pdict['CONTENT-LENGTH'] = int(self.headers['Content-Length'])
        if ctype == 'multipart/form-data':
            try:
                form = cgi.FieldStorage(fp=self.rfile, headers=self.headers, 
                                        environ={'REQUEST_METHOD':'POST', 'CONTENT_TYPE':self.headers['Content-Type']})
                # print(type(form))
                data = form["img"].file.read()
                
                image = np.array(cv2.imdecode(np.fromstring(data, dtype=np.uint8), cv2.IMREAD_COLOR))
                image = cv2.resize(image, dsize=(224, 224), interpolation=cv2.INTER_LINEAR)
                image_swap = np.swapaxes(image, 0, 2) # BGR -> RGB
                image_swap = np.expand_dims(image_swap, axis=0)

                tensor = torch.from_numpy(image_swap).type(torch.FloatTensor)
                output = model(tensor)
                _, pred = torch.max(output, dim=1)
            except Exception:
                return False, "Internal Server Error"
            return True, birds[pred.item()]
        
        return False, "Predict Failed"
    
try:
    with socketserver.TCPServer(("", PORT), myHandler) as httpd:
        print(f"Serving at port {PORT}...")
        httpd.serve_forever()
except KeyboardInterrupt:
    print("KeyboardInterrupt")