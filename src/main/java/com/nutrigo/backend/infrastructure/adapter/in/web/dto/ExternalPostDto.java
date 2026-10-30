package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ExternalPostDto(
        @Schema(example = "1", description = "Identificador del recurso en la API externa")
        Long id,

        @Schema(example = "1", description = "Identificador del usuario en la API externa")
        Long userId,

        @Schema(example = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", description = "Título de la publicación")
        String title,

        @Schema(example = "quia et suscipit suscipit recusandae consequuntur expedita et cum...", description = "Cuerpo o contenido de la publicación")
        String body
) {
}

