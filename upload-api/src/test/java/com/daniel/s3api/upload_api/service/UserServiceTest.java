package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.dto.UserRequestDTO;
import com.daniel.s3api.upload_api.dto.UserResponseDTO;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void saveUser_shouldReturnUserResponseDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setNome("Daniel");
        dto.setEmail("daniel@test.com");
        dto.setSenha("123");
        dto.setRole("USER");

        when(passwordEncoder.encode("123")).thenReturn("hashed-123");

        User user = new User();
        user.setId(1);
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha("hashed-123");
        user.setRole(dto.getRole());

        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.saveUser(dto);

        assertEquals("Daniel", response.nome());
        assertEquals("daniel@test.com", response.email());
        assertEquals("USER", response.role());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void saveUser_shouldEncodePasswordBeforeSaving() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setNome("Daniel");
        dto.setEmail("daniel@test.com");
        dto.setSenha("plainpass");
        dto.setRole("USER");

        when(passwordEncoder.encode("plainpass")).thenReturn("$2a$10$encoded");

        User saved = new User();
        saved.setId(1);
        saved.setNome("Daniel");
        saved.setEmail("daniel@test.com");
        saved.setSenha("$2a$10$encoded");
        saved.setRole("USER");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(saved);

        userService.saveUser(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());
        assertNotEquals("plainpass", captor.getValue().getSenha());
        verify(passwordEncoder).encode("plainpass");
    }

    @Test
    void authenticate_correctPassword_shouldReturnUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("daniel@test.com");
        user.setSenha("$2a$10$hashed");

        when(userRepository.findByEmail("daniel@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainpass", "$2a$10$hashed")).thenReturn(true);

        User result = userService.authenticate("daniel@test.com", "plainpass");

        assertEquals(1, result.getId());
    }

    @Test
    void authenticate_wrongPassword_shouldThrowException() {
        User user = new User();
        user.setId(1);
        user.setEmail("daniel@test.com");
        user.setSenha("$2a$10$hashed");

        when(userRepository.findByEmail("daniel@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hashed")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.authenticate("daniel@test.com", "wrongpass"));
        assertEquals("Email ou senha inválidos", ex.getMessage());
    }

    @Test
    void authenticate_unknownEmail_shouldThrowException() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.authenticate("unknown@test.com", "anypass"));
        assertEquals("Email ou senha inválidos", ex.getMessage());
    }

    @Test
    void listUsers_shouldReturnListOfUserResponseDTO() {
        User user1 = new User();
        user1.setId(1); user1.setNome("Daniel"); user1.setEmail("daniel@test.com"); user1.setRole("USER");

        User user2 = new User();
        user2.setId(2); user2.setNome("Maria"); user2.setEmail("maria@test.com"); user2.setRole("ADMIN");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserResponseDTO> users = userService.listUsers();

        assertEquals(2, users.size());
        assertEquals("Daniel", users.get(0).nome());
        assertEquals("Maria", users.get(1).nome());
    }

    @Test
    void searchUserById_existingId_shouldReturnUserResponseDTO() {
        User user = new User();
        user.setId(1);
        user.setNome("Daniel");
        user.setEmail("daniel@test.com");
        user.setRole("USER");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.searchUserById(1);

        assertEquals(1, response.id());
        assertEquals("Daniel", response.nome());
    }

    @Test
    void searchUserById_nonExistingId_shouldThrowException() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.searchUserById(99));
        assertEquals("Usuário não encontrado", exception.getMessage());
    }
}
