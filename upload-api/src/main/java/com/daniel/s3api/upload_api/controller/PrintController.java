package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;
import com.daniel.s3api.upload_api.service.PrintService;
import jakarta.servlet.http.HttpServletRequest;
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
            @RequestParam("bucketName") String bucketName,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        Print newPrint = printService.savePrint(file, game, description, userId, bucketName);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPrint);
    }

    @GetMapping
    public ResponseEntity<List<Print>> listPrints() {
        return ResponseEntity.ok(printService.listPrints());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Print>> listPrintsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(printService.listPrintsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Print> getPrintById(@PathVariable Long id) {
        Print print = printService.getPrintById(id);
        return ResponseEntity.ok(print);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Print> updatePrint(@PathVariable Long id,
                                             @RequestBody Print newPrint,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Print updated = printService.updatePrint(id, newPrint, userId);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<Print> updatePrintDescription(@PathVariable Long id,
                                                        @RequestParam String newDescription,
                                                        HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Print updated = printService.updatePrintDescription(id, newDescription, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrint(@PathVariable Long id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        printService.deletePrintById(id, userId);
        return ResponseEntity.noContent().build();
    }
}
