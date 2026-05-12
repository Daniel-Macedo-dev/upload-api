package com.daniel.s3api.upload_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String region;

    public S3Service(S3Client s3Client, @Value("${aws.s3.region}") String region) {
        this.s3Client = s3Client;
        this.region = region;
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Apenas imagens são permitidas (jpeg, png, gif, webp, etc.)");
        }

        String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        String key = UUID.randomUUID() + "_" + safeFilename;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar arquivo para S3", e);
        }
    }
}
