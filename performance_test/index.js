const express = require("express")
const multer = require("multer")
const path = require("path")
const fs = require("fs")

const app = express();
const uploader = multer({
    storage: multer.diskStorage({
        destination: function(req, file, cb){
            cb(null, 'uploads/')
        },
        filename: function(req, file, cb){
            cb(null, Date.now() + path.extname(file.originalname));
        }
    }),
    limits:{fileSize: 3 * 1024 * 1024}
})

const {execSync, exec, spawnSync, spawn} = require('child_process');

app.listen(3000, ()=>{
    console.log("init server...");
})

app.get("/test",(req, res)=> {
    const test = spawnSync("python3", ["./test.py"]);

    console.log(test.stdout.toString());
    // console.log(test.stderr.toString());

    res.send(test.stdout.toString());
})

app.post("/check/bird", uploader.single('img'), async (req, res, next)=>{
    // console.log(req.file);
    const imgFilePath = req.file.path;
    console.log(imgFilePath);

    const result = spawnSync("python3", ["./predict_model.py", imgFilePath]);
    console.log(result.stdout.toString());
    console.log(result.stderr.toString());

    // console.log(fs.existsSync(imgFilePath));
    if (fs.existsSync(imgFilePath))
        fs.rmSync(imgFilePath);

    res.status(200).send(result.stdout.toString());
})