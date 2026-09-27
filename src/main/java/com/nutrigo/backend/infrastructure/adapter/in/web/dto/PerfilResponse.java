package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.Meta;
import com.nutrigo.backend.domain.model.Usuario;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PerfilResponse(
        UUID id,
        String nombre,
        String email,
        BigDecimal estaturaCm,
        BigDecimal pesoKg,
        Meta meta,
        Instant fechaRegistro
) {

    public static PerfilResponse desde(Usuario usuario) {
        return new PerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getEstaturaCm(),
                usuario.getPesoKg(),
                usuario.getMeta(),
                usuario.getFechaRegistro());
    }
}
