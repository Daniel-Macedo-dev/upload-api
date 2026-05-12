package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final S3Service s3Service;
    private final String bucketName;

    public UploadController(S3Service s3Service, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Service = s3Service;
        this.bucketName = bucketName;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         HttpServletRequest request) throws Exception {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }

        String fileUrl = s3Service.uploadFile(file, bucketName);
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUrl);
    }
}
