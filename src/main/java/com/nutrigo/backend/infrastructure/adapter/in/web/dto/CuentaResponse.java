package com.nutrigo.backend.infrastructure.adapter.in.web.dto;

import com.nutrigo.backend.domain.model.Rol;
import com.nutrigo.backend.domain.model.UsuarioIdentidad;

import java.util.List;
import java.util.UUID;

/** Cuenta de Keycloak con sus roles, para la administración de roles. */
public record CuentaResponse(
        UUID keycloakUserId,
        String username,
        String email,
        String nombre,
        boolean habilitado,
        List<String> roles
) {

    public static CuentaResponse desde(UsuarioIdentidad cuenta) {
        return new CuentaResponse(
                cuenta.keycloakUserId(),
                cuenta.username(),
                cuenta.email(),
                cuenta.nombre(),
                cuenta.habilitado(),
                cuenta.roles().stream().map(Rol::name).sorted().toList());
    }
}
