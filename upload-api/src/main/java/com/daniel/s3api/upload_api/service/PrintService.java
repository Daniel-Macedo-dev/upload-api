package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.dto.PrintResponseDTO;
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

    private PrintResponseDTO toResponseDTO(Print print) {
        PrintResponseDTO dto = new PrintResponseDTO();
        dto.setId(print.getId());
        dto.setFilename(print.getFilename());
        dto.setGame(print.getGame());
        dto.setDescription(print.getDescription());
        dto.setUrl(print.getUrl());
        dto.setUploadDate(print.getUploadDate());
        dto.setUsername(print.getUser() != null ? print.getUser().getNome() : "Desconhecido");
        return dto;
    }

    public PrintResponseDTO savePrint(MultipartFile file, String game, String description, Integer userId) {
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

        return toResponseDTO(print);
    }

    public List<PrintResponseDTO> listPrints() {
        return printRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PrintResponseDTO> listPrintsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getPrints()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PrintResponseDTO getPrintById(Long id, Integer requesterId) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(requesterId)) {
            throw new RuntimeException("Unauthorized");
        }

        return toResponseDTO(print);
    }

    public PrintResponseDTO updatePrint(Long id, PrintResponseDTO dto, Integer userId, boolean isAdmin) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Unauthorized");
        }

        print.setGame(dto.getGame());
        print.setDescription(dto.getDescription());

        Print updated = printRepository.saveAndFlush(print);
        return toResponseDTO(updated);
    }

    public PrintResponseDTO updatePrintDescription(Long id, String newDescription, Integer userId, boolean isAdmin) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found"));

        if (!print.getUser().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Unauthorized");
        }

        print.setDescription(newDescription);
        Print updated = printRepository.saveAndFlush(print);
        return toResponseDTO(updated);
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
