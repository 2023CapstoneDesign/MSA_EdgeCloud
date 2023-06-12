package com.example.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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


    //@Value("${localhost:8080/check/bird}")
    private String predictModelUrl = "localhost:8080/check/bird";

    @PostMapping("/upload")
    public ResponseEntity<String> handleImageUpload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Content-Type") String contentType
    ) throws IOException {
        // Create a temporary file
        File tempFile = File.createTempFile("image", file.getOriginalFilename());
        file.transferTo(tempFile);

        // Prepare the request body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile));

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Create the request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send the request to the prediction model
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(predictModelUrl, requestEntity, String.class);

        // Delete the temporary file
        tempFile.delete();

        return responseEntity;
    }
}

