package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.PrintRepository;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PrintService {
    private final S3Service s3Service;
    private final PrintRepository printRepository;
    private final UserRepository userRepository;

    public PrintService(S3Service s3Service, PrintRepository printRepository, UserRepository userRepository) {
        this.s3Service = s3Service;
        this.printRepository = printRepository;
        this.userRepository = userRepository;
    }

    public Print uploadPrint(MultipartFile file, String game, String description, Integer userId, String bucketName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String fileUrl = s3Service.uploadFile(file, bucketName);

        Print print = new Print();
        print.setFilename(file.getOriginalFilename());
        print.setGame(game);
        print.setDescription(description);
        print.setUrl(fileUrl);

        user.addPrint(print);
        userRepository.save(user);

        return print;
    }
}
