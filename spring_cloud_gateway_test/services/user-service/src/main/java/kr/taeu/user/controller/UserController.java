package kr.taeu.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public Mono<String> get(final ServerHttpRequest request,
                            final ServerHttpResponse response) {
        log.info("User MSA Start");
        final HttpHeaders httpHeader = request.getHeaders();
        httpHeader.forEach((key, values) -> log.info("{}: {}", key, values));

        log.info("User MSA End");

        return Mono.just("User MSA Response");
    }

    @PostMapping
    public String processImage(@RequestParam("imageFile") MultipartFile imageFile) {
        try {
            CommonsMultipartResolver resolver = new CommonsMultipartResolver();
            resolver.setMaxUploadSize(10 * 1024 * 1024); // 10MB로 제한을 늘리는 예시

            // 이미지 파일을 임시 파일로 저장
            File tempFile = File.createTempFile("temp", imageFile.getOriginalFilename());
            imageFile.transferTo(tempFile);

            // Python 파일 경로
            String pythonFilePath = "C:/Users/qudtj/Desktop/capstone/MSA_EdgeCloud/performance_test/predict_model.py";

            // Python 인터프리터 실행 명령어 설정
            String[] command = {"python", pythonFilePath, tempFile.getAbsolutePath()};

            // 프로세스 빌더 생성
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // 작업 디렉토리 설정 (선택 사항)
            processBuilder.directory(new File("../performance_test/predict_model.py"));

            // 프로세스 실행
            Process process = processBuilder.start();

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            System.out.println("Exited with error code: " + exitCode);

            // 임시 파일 삭제
            tempFile.delete();

            return "Image processing completed successfully.";

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Image processing failed.";
        }

    }
}
