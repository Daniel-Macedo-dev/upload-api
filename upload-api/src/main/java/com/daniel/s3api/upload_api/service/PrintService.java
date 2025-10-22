package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.dto.PrintDTO;
import com.daniel.s3api.upload_api.infrastructure.entities.Print;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.PrintRepository;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public PrintDTO savePrint(MultipartFile file, String game, String description, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileUrl = s3Service.uploadFile(file, "prints-jogos");

        Print print = new Print();
        print.setFilename(file.getOriginalFilename());
        print.setGame(game);
        print.setDescription(description);
        print.setUrl(fileUrl);
        print.setUploadDate(LocalDateTime.now());
        print.setUser(user);

        user.addPrint(print);
        userRepository.saveAndFlush(user);

        return new PrintDTO(print);
    }

    public List<PrintDTO> listPrints() {
        return printRepository.findAll()
                .stream()
                .map(PrintDTO::new)
                .collect(Collectors.toList());
    }

    public List<PrintDTO> listPrintsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getPrints()
                .stream()
                .map(PrintDTO::new)
                .collect(Collectors.toList());
    }

    public PrintDTO getPrintById(Long id, Integer requesterId) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(requesterId)) {
            throw new RuntimeException("Unauthorized");
        }

        return new PrintDTO(print);
    }

    public PrintDTO updatePrint(Long id, PrintDTO dto, Integer userId, boolean isAdmin) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Unauthorized");
        }

        print.setGame(dto.getGame());
        print.setDescription(dto.getDescription());

        Print updated = printRepository.saveAndFlush(print);
        return new PrintDTO(updated);
    }

    public PrintDTO updatePrintDescription(Long id, String newDescription, Integer userId, boolean isAdmin) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Unauthorized");
        }

        print.setDescription(newDescription);
        Print updated = printRepository.saveAndFlush(print);
        return new PrintDTO(updated);
    }

    public void deletePrintById(Long id, Integer userId, boolean isAdmin) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(userId) && !isAdmin) {
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
    public void deleteAllPrints() {
        printRepository.deleteAll();
    }

}
