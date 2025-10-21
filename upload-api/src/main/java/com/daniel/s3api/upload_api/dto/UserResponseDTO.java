package com.daniel.s3api.upload_api.dto;

public class UserResponseDTO {
    private Integer id;
    private String nome;
    private String email;
    private String role;

    public UserResponseDTO(Integer id, String nome, String email, String role) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
