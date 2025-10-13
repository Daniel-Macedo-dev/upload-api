package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.service.UserService;
import com.daniel.s3api.upload_api.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        User created = userService.saveUser(user);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        User foundUser = userService.authenticate(user.getEmail(), user.getSenha());
        String token = jwtService.generateToken(foundUser.getId());
        return ResponseEntity.ok(token);
    }
}
