package com.nutrigo.backend.domain.model;

/**
 * Roles de realm definidos en Keycloak. NutriGO no los persiste: Keycloak es
 * la única fuente de verdad (ver docs/RUTA_DESARROLLO.md, Fase 2).
 */
public enum Rol {
    USER,
    NUTRITIONIST,
    ADMIN;

    /** Roles que un administrador puede otorgar o retirar desde la app. */
    public boolean esAsignable() {
        return this == NUTRITIONIST || this == ADMIN;
    }
}
