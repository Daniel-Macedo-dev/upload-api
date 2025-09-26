package com.daniel.s3api.upload_api.Controller;

import com.daniel.s3api.upload_api.Service.S3Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController {
    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {this.s3Service = s3Service;}

    @PostMapping("/upload")
    public String upload(@RequestParam("file")MultipartFile file) throws Exception{
        String bucketName = "prints-jogos";
        return s3Service.uploadFile(file, bucketName);
    }
}
