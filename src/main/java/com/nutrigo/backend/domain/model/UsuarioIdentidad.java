package com.nutrigo.backend.domain.model;

import java.util.Set;
import java.util.UUID;

/** Cuenta tal como existe en el proveedor de identidad (Keycloak), con sus roles. */
public record UsuarioIdentidad(
        UUID keycloakUserId,
        String username,
        String email,
        String nombre,
        boolean habilitado,
        Set<Rol> roles
) {
}
