const express = require("express");
const multer = require("multer");
const path = require("path");
const { v4: uuidv4 } = require("uuid");
const fs = require("fs");
const request = require("request");

const app = express();
const uploader = multer({
  storage: multer.diskStorage({
    destination: function (req, file, cb) {
      if (!fs.existsSync("uploads/")) {
        fs.mkdirSync("uploads/");
      }
      cb(null, "uploads/");
    },
    filename: function (req, file, cb) {
      cb(null, Date.now() + `_${uuidv4()}_` + path.extname(file.originalname));
    },
  }),
  limits: { fileSize: 3 * 1024 * 1024 },
});

app.post("/predict", uploader.single("img"), (req, resp) => {
  const t = Date.now();
  const options = {
    method: "POST",
    url: "http://torch:30080",
    // url: "http://nodejs-torch:30080",
    // hostname: nodejs-torch
    formData: {
      img: fs.createReadStream(req.file.path),
    },
  };
  request(options, function (err, res, body) {
    if (fs.existsSync(req.file.path)) fs.rmSync(req.file.path);
    if (err) resp.send(err);
    else {
      resp.send({
        result: body,
        elapsed: Date.now() - t,
      });
    }
  });
});

app.listen(3000, () => {
  console.log("init server...");
});
