package com.nutrigo.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Reporte generado para un usuario. Igual que {@link RegistroProgreso}, es
 * histórico (append-only): cada llamada crea una fotografía nueva, nunca se
 * actualiza una existente.
 */
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoReporte tipo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false, updatable = false)
    private Instant fecha;

    protected Reporte() {
        // Requerido por JPA
    }

    public Reporte(Usuario usuario, TipoReporte tipo, String contenido) {
        this.usuario = Objects.requireNonNull(usuario, "usuario es obligatorio");
        this.tipo = Objects.requireNonNull(tipo, "tipo es obligatorio");
        this.contenido = Objects.requireNonNull(contenido, "contenido es obligatorio");
        this.fecha = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public TipoReporte getTipo() {
        return tipo;
    }

    public String getContenido() {
        return contenido;
    }

    public Instant getFecha() {
        return fecha;
    }
}
