package com.daniel.s3api.upload_api.controller;

import com.daniel.s3api.upload_api.dto.UserRequestDTO;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.service.JwtService;
import com.daniel.s3api.upload_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserService userService;
    private JwtService jwtService;
    private AuthController authController;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        authController = new AuthController(userService, jwtService);
    }

    @Test
    void testSignup() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setNome("Daniel");
        dto.setEmail("daniel@test.com");
        dto.setSenha("1234");

        when(userService.saveUser(dto)).thenReturn(null);

        ResponseEntity<?> response = authController.signup(dto);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).saveUser(dto);
    }

    @Test
    void testLogin() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("a@b.com");
        dto.setSenha("1234");

        User user = new User();
        user.setId(1);

        when(userService.authenticate("a@b.com", "1234")).thenReturn(user);
        when(jwtService.generateToken(1)).thenReturn("fake-token");

        ResponseEntity<String> response = authController.login(dto);
        assertEquals("fake-token", response.getBody());
    }
}
