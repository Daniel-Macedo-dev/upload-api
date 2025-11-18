package com.daniel.s3api.upload_api.dto;

import com.daniel.s3api.upload_api.infrastructure.entities.User;

public record UserResponseDTO(
        Integer id,
        String nome,
        String email,
        String role
) {
    public UserResponseDTO(User user) {
        this(user.getId(), user.getNome(), user.getEmail(), user.getRole());
    }
}
