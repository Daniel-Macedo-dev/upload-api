package com.daniel.s3api.upload_api.dto;

import java.time.LocalDateTime;

public class PrintResponseDTO {
    private Long id;
    private String filename;
    private String game;
    private String description;
    private String url;
    private LocalDateTime uploadDate;
    private String username;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getFilename() {return filename;}

    public void setFilename(String filename) {this.filename = filename;}

    public String getGame() {return game;}

    public void setGame(String game) {this.game = game;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getUrl() {return url;}

    public void setUrl(String url) {this.url = url;}

    public LocalDateTime getUploadDate() {return uploadDate;}

    public void setUploadDate(LocalDateTime uploadDate) {this.uploadDate = uploadDate;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}
}
