package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;
import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.PrintRepository;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PrintServiceTest {

    private PrintRepository printRepository;
    private UserRepository userRepository;
    private S3Service s3Service;
    private PrintService printService;

    @BeforeEach
    void setup() {
        printRepository = mock(PrintRepository.class);
        userRepository = mock(UserRepository.class);
        s3Service = mock(S3Service.class);
        printService = new PrintService(s3Service, printRepository, userRepository);
    }

    @Test
    void testSavePrint() throws Exception {
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn("http://fake-url.com");
        when(printRepository.saveAndFlush(any(Print.class))).thenAnswer(i -> i.getArguments()[0]);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("print.png");

        var dto = printService.savePrint(file, "GameX", "Descrição", 1);

        assertEquals("GameX", dto.getGame());
        assertEquals("Descrição", dto.getDescription());
        assertEquals("http://fake-url.com", dto.getUrl());
        verify(printRepository, times(1)).saveAndFlush(any(Print.class));
    }
}
