// repeat same HTTP request to server 50 times;

const fs = require("fs");
const FormData = require("form-data");
const axios = require("axios");

if (process.argv.length != 4){
    throw Error("usage: node repeat50.js [SERVER IP ADDRESS:PORT] [IMG FILE PATH]");
}

var serverAddress = process.argv[2];
var port = 8080;

// (async () => {

//     promis = []
//     for (let i = 0; i < 5; i++){

//         const data = new FormData();
//         data.append("img", fs.createReadStream(process.argv[3]));

//         const config = {
//             method: 'post',
//             url:`http://${serverAddress}/check/bird`,
//             port,
//             data: data
//         }
//         promis.push(new Promise(async(res, rej)=>{
//             await axios(config)
//             .then((res)=>{
//                 res= res.data;
//                 console.log(res);
//             })
//             res(0);
//         }))
//     }

//     const startTime = Date.now();

//     await Promise.all(promis);

//     const endTime = Date.now();

//     console.log(`${(endTime - startTime) / 1000.0} second(s)`);
// }) ();

(async () => {

    cons = []
    for (let i = 0; i < 5; i++){

        const data = new FormData();
        data.append("img", fs.createReadStream(process.argv[3]));

        const config = {
            method: 'post',
            url:`http://${serverAddress}/check/bird`,
            port,
            data: data
        }
        cons.push(config);
    }

    const startTime = Date.now();

    for (var i = 0; i < 5; i++){
        let result = (await axios(cons[i])).data;

        console.log(result);
    }

    const endTime = Date.now();

    console.log(`${(endTime - startTime) / 1000.0} seconds`);
}) ();

