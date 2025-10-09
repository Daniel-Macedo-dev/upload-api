package com.daniel.s3api.upload_api.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Print() {}

    public Print(String filename, String game, String description, LocalDateTime uploadDate) {
        this.filename = filename;
        this.game = game;
        this.description = description;
        this.uploadDate = uploadDate;
    }

    @PrePersist
    public void prePersist(){
        this.uploadDate = LocalDateTime.now();
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getFilename() {return filename;}

    public void setFilename(String filename) {this.filename = filename;}

    public String getGame() {return game;}

    public void setGame(String game) {this.game = game;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public LocalDateTime getUploadDate() {return uploadDate;}

    public void setUploadDate(LocalDateTime uploadDate) {this.uploadDate = uploadDate;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}
}
