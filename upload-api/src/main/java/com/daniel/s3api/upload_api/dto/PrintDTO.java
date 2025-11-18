package com.daniel.s3api.upload_api.dto;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;

public record PrintDTO(
        Long id,
        String filename,
        String game,
        String description,
        String url,
        String userName
) {
    public PrintDTO(Print print) {
        this(
                print.getId(),
                print.getFilename(),
                print.getGame(),
                print.getDescription(),
                print.getUrl(),
                print.getUser() != null ? print.getUser().getNome() : "Desconhecido"
        );
    }
}
