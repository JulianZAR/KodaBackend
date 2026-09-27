package com.nutrigo.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Punto histórico de peso/IMC de un {@link Usuario}. A diferencia del perfil
 * (que solo cachea el último valor), aquí sí se persiste el IMC calculado:
 * es una fotografía inmutable de un momento pasado, no un valor "actual" que
 * pueda desincronizarse.
 */
@Entity
@Table(name = "registros_progreso")
public class RegistroProgreso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "peso_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal imc;

    @Column(nullable = false, updatable = false)
    private Instant fecha;

    protected RegistroProgreso() {
        // Requerido por JPA
    }

    public RegistroProgreso(Usuario usuario, BigDecimal pesoKg, BigDecimal imc) {
        this.usuario = Objects.requireNonNull(usuario, "usuario es obligatorio");
        this.pesoKg = Objects.requireNonNull(pesoKg, "pesoKg es obligatorio");
        this.imc = Objects.requireNonNull(imc, "imc es obligatorio");
        this.fecha = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public BigDecimal getImc() {
        return imc;
    }

    public Instant getFecha() {
        return fecha;
    }
}
