package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.service.ExternalApiService;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.ExternalPostDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external")
@Tag(name = "API Externa", description = "Consumo de servicios externos con trazabilidad de logs")
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    public ExternalApiController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/info")
    @Operation(summary = "Obtiene información de una API REST externa con registro de logs")
    public ResponseEntity<ExternalPostDto> obtenerInfoExterna() {
        ExternalPostDto info = externalApiService.obtenerInformacionExterna();
        return ResponseEntity.ok(info);
    }
}

