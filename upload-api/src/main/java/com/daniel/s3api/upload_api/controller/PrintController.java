package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;
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
    public ResponseEntity<Print> uploadPrint(
            @RequestParam("file") MultipartFile file,
            @RequestParam("game") String game,
            @RequestParam("description") String description,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Print newPrint = printService.savePrint(file, game, description, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPrint);
    }

    @GetMapping
    public ResponseEntity<List<Print>> listAllPrints() {
        return ResponseEntity.ok(printService.listPrints());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Print>> listPrintsByUser(@PathVariable Integer userId,
                                                        HttpServletRequest request) {
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!requesterId.equals(userId) && !userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(printService.listPrintsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Print> getPrintById(@PathVariable Long id,
                                              HttpServletRequest request) {
        Print print = printService.getPrintById(id);
        Integer userId = (Integer) request.getAttribute("userId");

        if (!print.getUser().getId().equals(userId) && !userService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(print);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Print> updatePrint(@PathVariable Long id,
                                             @RequestBody Print newPrint,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        boolean isAdmin = userService.isAdmin(userId);

        Print updated = printService.updatePrint(id, newPrint, userId, isAdmin);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<Print> updatePrintDescription(@PathVariable Long id,
                                                        @RequestParam String newDescription,
                                                        HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        boolean isAdmin = userService.isAdmin(userId);

        Print updated = printService.updatePrintDescription(id, newDescription, userId, isAdmin);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrint(@PathVariable Long id,
                                            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        boolean isAdmin = userService.isAdmin(userId);

        printService.deletePrintById(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
