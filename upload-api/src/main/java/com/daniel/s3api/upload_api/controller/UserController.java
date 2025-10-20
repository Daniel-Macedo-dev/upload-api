package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.infrastructure.entities.User;
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
    public ResponseEntity<User> save(@RequestBody User user, HttpServletRequest request) {
        Integer requesterId = (Integer) request.getAttribute("userId");
        boolean isAdmin = userService.isAdmin(requesterId);

        if (!isAdmin) {
            user.setRole("USER");
        }
        User newUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll(HttpServletRequest request){
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.listUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Integer id, HttpServletRequest request){
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!requesterId.equals(id) && !userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = userService.searchUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id,
                                           @RequestBody User newUser,
                                           HttpServletRequest request){
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!requesterId.equals(id) && !userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User atualizado = userService.updateUser(id, newUser);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id, HttpServletRequest request){
        Integer requesterId = (Integer) request.getAttribute("userId");
        if (!requesterId.equals(id) && !userService.isAdmin(requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
