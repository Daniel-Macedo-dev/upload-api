package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;
import com.daniel.s3api.upload_api.service.PrintService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/prints")
public class PrintController {

    private final PrintService printService;

    public PrintController(PrintService printService) {
        this.printService = printService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Print> uploadPrint(
            @RequestParam("file") MultipartFile file,
            @RequestParam("game") String game,
            @RequestParam("description") String description,
            @RequestParam("userId") Integer userId,
            @RequestParam("bucketName") String bucketName) {

        Print novoPrint = printService.salvarPrint(file, game, description, userId, bucketName);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPrint);
    }

    @GetMapping
    public ResponseEntity<List<Print>> listarPrints() {
        return ResponseEntity.ok(printService.listarPrints());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Print>> listarPrintsPorUsuario(@PathVariable Integer userId) {
        return ResponseEntity.ok(printService.listarPrintsPorUsuario(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Print> buscarPorId(@PathVariable Long id) {
        Print print = printService.buscarPrintPorId(id);
        return ResponseEntity.ok(print);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Print> substituirPrint(@PathVariable Long id, @RequestBody Print novaPrint) {
        Print atualizado = printService.substituirPrint(id, novaPrint);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}/descricao")
    public ResponseEntity<Print> atualizarDescricao(@PathVariable Long id, @RequestParam String novaDescricao) {
        Print atualizado = printService.atualizarPrintDescricao(id, novaDescricao);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPrint(@PathVariable Long id) {
        printService.deletarPrintPorId(id);
        return ResponseEntity.noContent().build();
    }
}