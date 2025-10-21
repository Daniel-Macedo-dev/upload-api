package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.dto.UserRequestDTO;
import com.daniel.s3api.upload_api.dto.UserResponseDTO;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserResponseDTO saveUser(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        String encryptedPassword = passwordEncoder.encode(dto.getSenha());
        User user = new User(dto.getNome(), dto.getEmail(), encryptedPassword, dto.getRole());
        User saved = userRepository.save(user);

        return new UserResponseDTO(saved.getId(), saved.getNome(), saved.getEmail(), saved.getRole());
    }

    public List<UserResponseDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getRole()))
                .collect(Collectors.toList());
    }

    public UserResponseDTO searchUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UserResponseDTO(user.getId(), user.getNome(), user.getEmail(), user.getRole());
    }

    public UserResponseDTO updateUser(Integer id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dto.getNome() != null) user.setNome(dto.getNome());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getSenha() != null) user.setSenha(passwordEncoder.encode(dto.getSenha()));
        if (dto.getRole() != null) user.setRole(dto.getRole());

        User updated = userRepository.save(user);
        return new UserResponseDTO(updated.getId(), updated.getNome(), updated.getEmail(), updated.getRole());
    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }

    public boolean isAdmin(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent() && "ADMIN".equalsIgnoreCase(user.get().getRole());
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
