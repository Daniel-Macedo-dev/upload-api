package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        if (user.getRole() == null) user.setRole("USER");
        return userRepository.saveAndFlush(user);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User searchUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Integer id, User newUser) {
        User userSearch = searchUserById(id);
        userSearch.setNome(newUser.getNome());
        userSearch.setEmail(newUser.getEmail());
        return userRepository.saveAndFlush(userSearch);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public User authenticate(String email, String senha) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(senha, user.getSenha())) {
            throw new RuntimeException("Incorrect password");
        }
        return user;
    }

    public boolean isAdmin(Integer userId) {
        User user = searchUserById(userId);
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }
}
