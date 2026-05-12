package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.dto.UserRequestDTO;
import com.daniel.s3api.upload_api.dto.UserResponseDTO;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO saveUser(UserRequestDTO dto) {
        User user = new User();
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        user.setRole(dto.getRole() != null ? dto.getRole() : "USER");

        User saved = userRepository.saveAndFlush(user);
        return new UserResponseDTO(saved);
    }

    public User authenticate(String email, String senha) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));
        if (!passwordEncoder.matches(senha, user.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos");
        }
        return user;
    }

    public boolean isAdmin(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public List<UserResponseDTO> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UserResponseDTO searchUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UserResponseDTO(user);
    }

    public UserResponseDTO updateUser(Integer id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(passwordEncoder.encode(dto.getSenha()));
        user.setRole(dto.getRole());

        User updated = userRepository.saveAndFlush(user);
        return new UserResponseDTO(updated);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
