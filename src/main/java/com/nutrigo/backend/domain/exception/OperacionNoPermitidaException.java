package com.nutrigo.backend.domain.exception;

/** Regla de negocio que impide la operación pedida (se responde con 409). */
public class OperacionNoPermitidaException extends RuntimeException {

    public OperacionNoPermitidaException(String message) {
        super(message);
    }
}
