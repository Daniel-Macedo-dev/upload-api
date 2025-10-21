package com.daniel.s3api.upload_api.dto;

import com.daniel.s3api.upload_api.infrastructure.entities.User;

public class UserResponseDTO {
    private Integer id;
    private String nome;
    private String email;
    private String role;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
