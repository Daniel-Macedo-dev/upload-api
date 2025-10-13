package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.PrintRepository;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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

    public Print savePrint(MultipartFile file, String game, String description, Integer userId, String bucketName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileUrl = s3Service.uploadFile(file, bucketName);

        Print print = new Print();
        print.setFilename(file.getOriginalFilename());
        print.setGame(game);
        print.setDescription(description);
        print.setUrl(fileUrl);
        print.setUploadDate(LocalDateTime.now());
        print.setUser(user);

        user.addPrint(print);
        userRepository.saveAndFlush(user);

        return print;
    }

    public List<Print> listPrints() {
        return printRepository.findAll();
    }

    public List<Print> listPrintsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPrints();
    }

    public Print getPrintById(Long id) {
        return printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));
    }

    public Print updatePrint(Long id, Print newPrint, Integer userId) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        print.setFilename(newPrint.getFilename());
        print.setGame(newPrint.getGame());
        print.setDescription(newPrint.getDescription());
        print.setUrl(newPrint.getUrl());

        return printRepository.saveAndFlush(print);
    }

    public Print updatePrintDescription(Long id, String newDescription, Integer userId) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        print.setDescription(newDescription);
        return printRepository.saveAndFlush(print);
    }

    public void deletePrintById(Long id, Integer userId) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        User user = print.getUser();
        if (user != null) {
            user.removePrint(print);
            userRepository.saveAndFlush(user);
        } else {
            printRepository.deleteById(id);
        }
    }
}
