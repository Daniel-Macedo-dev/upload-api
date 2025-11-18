package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.dto.UserRequestDTO;
import com.daniel.s3api.upload_api.dto.UserResponseDTO;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void saveUser_shouldReturnUserResponseDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setNome("Daniel");
        dto.setEmail("daniel@test.com");
        dto.setSenha("123");
        dto.setRole("USER");

        User user = new User();
        user.setId(1);
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setSenha(dto.getSenha());
        user.setRole(dto.getRole());

        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.saveUser(dto);

        assertEquals("Daniel", response.nome());
        assertEquals("daniel@test.com", response.email());
        assertEquals("USER", response.role());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
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
