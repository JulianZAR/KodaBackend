package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.domain.exception.DatosInvalidosException;
import com.nutrigo.backend.domain.exception.PerfilIncompletoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatosInvalidosException.class)
    public ProblemDetail manejarDatosInvalidos(DatosInvalidosException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PerfilIncompletoException.class)
    public ProblemDetail manejarPerfilIncompleto(PerfilIncompletoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
}
