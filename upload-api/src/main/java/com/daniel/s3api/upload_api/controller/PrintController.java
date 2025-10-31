package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.dto.PrintResponseDTO;
import com.daniel.s3api.upload_api.service.PrintService;
import com.daniel.s3api.upload_api.service.UserService;
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
    private final UserService userService;

    public PrintController(PrintService printService, UserService userService) {
        this.printService = printService;

        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<PrintResponseDTO> uploadPrint(
            @RequestParam("file") MultipartFile file,
            @RequestParam("game") String game,
            @RequestParam("description") String description,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PrintResponseDTO newPrint = printService.savePrint(file, game, description, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPrint);
    }

    @GetMapping
    public ResponseEntity<List<PrintResponseDTO>> listAllPrints() {
        return ResponseEntity.ok(printService.listPrints());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PrintResponseDTO>> listPrintsByUser(
            @PathVariable Integer userId,
            HttpServletRequest request) {

        Integer requesterId = (Integer) request.getAttribute("userId");
        if (requesterId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!requesterId.equals(userId) && !userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(printService.listPrintsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrintResponseDTO> getPrintById(
            @PathVariable Long id,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PrintResponseDTO print = printService.getPrintById(id, userId);
        return ResponseEntity.ok(print);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrintResponseDTO> updatePrint(
            @PathVariable Long id,
            @RequestBody PrintResponseDTO newPrint,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isAdmin = userService.isAdmin(userId);
        PrintResponseDTO updated = printService.updatePrint(id, newPrint, userId, isAdmin);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<PrintResponseDTO> updatePrintDescription(
            @PathVariable Long id,
            @RequestParam String newDescription,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isAdmin = userService.isAdmin(userId);
        PrintResponseDTO updated = printService.updatePrintDescription(id, newDescription, userId, isAdmin);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrint(
            @PathVariable Long id,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isAdmin = userService.isAdmin(userId);
        printService.deletePrintById(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllPrints(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null || !userService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        printService.deleteAllPrints();
        return ResponseEntity.noContent().build();
    }
}
