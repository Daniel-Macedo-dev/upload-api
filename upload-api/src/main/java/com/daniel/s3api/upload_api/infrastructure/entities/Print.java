package com.daniel.s3api.upload_api.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "print_table")
@Entity
public class Print {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String game;
    private String description;
    private LocalDateTime uploadDate;

    private

    public Print() {}



    @PrePersist
    public void prePersist(){
        this.uploadDate = LocalDateTime.now();
    }
}
