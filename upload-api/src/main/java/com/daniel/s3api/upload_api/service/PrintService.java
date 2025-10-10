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

    public Print salvarPrint(MultipartFile file, String game, String description, Integer userId, String bucketName) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));

        String fileUrl = s3Service.uploadFile(file, bucketName);

        Print print = new Print();
        print.setFilename(file.getOriginalFilename());
        print.setGame(game);
        print.setDescription(description);
        print.setUrl(fileUrl);
        print.setUploadDate(LocalDateTime.now());

        user.addPrint(print);
        userRepository.saveAndFlush(user);

        return print;
    }

    public List<Print> listarPrints() {
        return printRepository.findAll();
    }

    public List<Print> listarPrintsPorUsuario(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));
        return user.getPrints();
    }

    public Print buscarPrintPorId(Long id) {
        return printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print não encontrada"));
    }

    public Print substituirPrint(Long id, Print novaPrint) {
        Print printBusca = printRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Print não encontrada"));

        printBusca.setFilename(novaPrint.getFilename());
        printBusca.setGame(novaPrint.getGame());
        printBusca.setDescription(novaPrint.getDescription());
        printBusca.setUrl(novaPrint.getUrl());

        return printRepository.saveAndFlush(printBusca);
    }

    public Print atualizarPrintDescricao(Long id, String novaDescricao) {
        Print printBusca = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print não encontrada"));

        printBusca.setDescription(novaDescricao);

        return printRepository.saveAndFlush(printBusca);
    }

    public void deletarPrintPorId(Long id) {
        Print printBusca = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print não encontrada"));

        User user = printBusca.getUser();
        if (user != null) {
            user.removePrint(printBusca);
            userRepository.saveAndFlush(user);
        } else {
            printRepository.deleteById(id);
        }
    }
}
