package com.daniel.s3api.upload_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3ServiceTest {

    private S3Client s3Client;
    private S3Service s3Service;

    @BeforeEach
    void setup() {
        s3Client = mock(S3Client.class);
        s3Service = new S3Service(s3Client, "sa-east-1");
    }

    @Test
    void uploadFile_emptyFile_shouldThrowIllegalArgumentException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> s3Service.uploadFile(file, "bucket"));
        verifyNoInteractions(s3Client);
    }

    @Test
    void uploadFile_nullFile_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> s3Service.uploadFile(null, "bucket"));
        verifyNoInteractions(s3Client);
    }

    @Test
    void uploadFile_nonImageContentType_shouldThrowIllegalArgumentException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("document.pdf");
        when(file.getContentType()).thenReturn("application/pdf");

        assertThrows(IllegalArgumentException.class, () -> s3Service.uploadFile(file, "bucket"));
        verifyNoInteractions(s3Client);
    }

    @Test
    void uploadFile_validImage_shouldCallS3AndReturnUrl() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("photo.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});

        String url = s3Service.uploadFile(file, "test-bucket");

        assertNotNull(url);
        assertTrue(url.startsWith("https://test-bucket.s3.sa-east-1.amazonaws.com/"));
        assertTrue(url.contains("photo.jpg"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadFile_validImage_keyMustBeUnique() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("photo.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});

        String url1 = s3Service.uploadFile(file, "bucket");
        String url2 = s3Service.uploadFile(file, "bucket");

        assertNotEquals(url1, url2, "Each upload must produce a unique key");
    }

    @Test
    void uploadFile_filenameWithSpaces_shouldSanitizeKey() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("my photo 2024.png");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getBytes()).thenReturn(new byte[]{1});

        String url = s3Service.uploadFile(file, "bucket");

        assertFalse(url.contains(" "), "URL must not contain raw spaces");
    }
}
