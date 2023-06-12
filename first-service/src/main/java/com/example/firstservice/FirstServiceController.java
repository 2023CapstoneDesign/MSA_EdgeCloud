package com.example.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@Slf4j
@RestController
@RequestMapping("/first-service")
public class FirstServiceController {

    Environment env;

    @Autowired
    public FirstServiceController(Environment env) {
        this.env = env;
    }
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First service.";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        log.info(header);
        return "Welcome to the first service.";
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        log.info("Server port = {}", request.getServerPort());
        return String.format("This is a message from First Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/bird")
    public String bird(@RequestBody MultipartFile imageFile) {
        try {
            // 임시 폴더에 이미지 저장
            File tempFile = File.createTempFile("image", imageFile.getOriginalFilename());
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(tempFile))) {
                stream.write(imageFile.getBytes());
            }

            // Python 파일 경로
            String pythonScriptPath = "../../../../../../../performance_test/predict_model.py";

            // 파라미터 전달을 위해 명령어 준비
            String[] cmd = new String[3];
            cmd[0] = "python"; // Python 인터프리터
            cmd[1] = pythonScriptPath; // Python 파일 경로
            cmd[2] = tempFile.getAbsolutePath(); // 이미지 파일 경로

            // 명령어 실행
            Process process = Runtime.getRuntime().exec(cmd);

            // 실행 결과 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 프로세스 종료 대기
            int exitCode = process.waitFor();

            // 임시 파일 삭제
            tempFile.delete();

            // 결과 반환
            return "Python script executed with output:\n" + output.toString() + "\nExit code: " + exitCode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error processing the image.";
        }
    }
}

