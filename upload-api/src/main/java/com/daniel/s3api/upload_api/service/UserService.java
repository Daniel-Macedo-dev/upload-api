package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String JWT_SECRET = "CoxinhaPrintS3";
    private final long JWT_EXPIRATION = 1000 * 60 * 60 * 24;

    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}

    public User salveUser(User user) {
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        return userRepository.saveAndFlush(user);
    }
    public List<User> listUsers (){
        return userRepository.findAll();
    }
    public User searchUserById(Integer id){
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));
    }
    public User updateUser (Integer id, User newUser){
        User userSearch = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));
        userSearch.setNome(newUser.getNome());
        userSearch.setEmail(newUser.getEmail());
        return userRepository.saveAndFlush(userSearch);
    }
    public void deleteUser (Integer id){
        userRepository.deleteById(id);
    }

    public String login (String email, String senha){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, user.getSenha())){
            throw new RuntimeException("Senha incorreta");
        }
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
}
