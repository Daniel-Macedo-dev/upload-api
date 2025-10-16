package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.service.JwtService;
import com.daniel.s3api.upload_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {this.userService = userService;this.jwtService = jwtService;}

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user){
        User newUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    @GetMapping
    public ResponseEntity<List<User>> findAll(){
        return ResponseEntity.ok(userService.listUsers());
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Integer id){
        User user = userService.searchUserById(id);

        return ResponseEntity.ok(user);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id,
                                           @RequestBody User newUser){
        User atualizado = userService.updateUser(id, newUser);
        return ResponseEntity.ok(atualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        User authenticatedUser = userService.authenticate(request.getEmail(), request.getSenha());
        String token = jwtService.generateToken(authenticatedUser.getId());
        return ResponseEntity.ok(token);
    }

    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() {return email;}

        public void setEmail(String email) {this.email = email;}

        public String getSenha() {return senha;}

        public void setSenha(String senha) {this.senha = senha;}
    }


}
