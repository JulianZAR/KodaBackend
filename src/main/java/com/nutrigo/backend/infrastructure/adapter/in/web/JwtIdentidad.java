package com.nutrigo.backend.infrastructure.adapter.in.web;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

/**
 * Extrae del JWT de Keycloak los datos de identidad que el backend necesita
 * para el aprovisionamiento JIT. Nunca se leen de un cuerpo de petición: la
 * identidad la posee Keycloak, no NutriGO.
 */
final class JwtIdentidad {

    private JwtIdentidad() {
    }

    static UUID keycloakUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    static String email(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }

    static String nombre(Jwt jwt) {
        String nombreCompleto = jwt.getClaimAsString("name");
        if (nombreCompleto != null && !nombreCompleto.isBlank()) {
            return nombreCompleto;
        }
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        return preferredUsername != null ? preferredUsername : email(jwt);
    }
}
