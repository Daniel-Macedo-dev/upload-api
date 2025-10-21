package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.dto.UserRequestDTO;
import com.daniel.s3api.upload_api.dto.UserResponseDTO;
import com.daniel.s3api.upload_api.service.JwtService;
import com.daniel.s3api.upload_api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> save(@RequestBody UserRequestDTO dto, HttpServletRequest request) {
        Integer requesterId = (Integer) request.getAttribute("userId");
        boolean isAdmin = userService.isAdmin(requesterId);

        if (!isAdmin) {
            dto.setRole("USER");
        }

        UserResponseDTO saved = userService.saveUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll(HttpServletRequest request) {
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UserResponseDTO> users = userService.listUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Integer id, HttpServletRequest request) {
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!requesterId.equals(id) && !userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponseDTO user = userService.searchUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer id,
                                                      @RequestBody UserRequestDTO dto,
                                                      HttpServletRequest request) {
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!requesterId.equals(id) && !userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponseDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id, HttpServletRequest request) {
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!requesterId.equals(id) && !userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
