package com.daniel.s3api.upload_api.dto;
import com.daniel.s3api.upload_api.infrastructure.entities.Print;

public class PrintDTO {
    private Long id;
    private String filename;
    private String game;
    private String description;
    private String url;
    private String userName;

    public PrintDTO(Print print) {
        this.id = print.getId();
        this.filename = print.getFilename();
        this.game = print.getGame();
        this.description = print.getDescription();
        this.url = print.getUrl();
        this.userName = print.getUser() != null ? print.getUser().getNome() : "Desconhecido";
    }

    public Long getId() { return id; }
    public String getFilename() { return filename; }
    public String getGame() { return game; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getUserName() { return userName; }
}