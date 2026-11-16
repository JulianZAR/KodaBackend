package com.nutrigo.backend.domain.exception;

/** El proveedor de identidad (Keycloak) no respondió o rechazó la operación (503). */
public class IdentidadNoDisponibleException extends RuntimeException {

    public IdentidadNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
