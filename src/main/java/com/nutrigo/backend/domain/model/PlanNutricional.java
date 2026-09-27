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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Plan nutricional vigente de un usuario. Es 1-a-1 con {@link Usuario} (no es
 * histórico como {@link RegistroProgreso}): asignarlo de nuevo reemplaza el
 * anterior, siempre asignado por un NUTRITIONIST o ADMIN.
 */
@Entity
@Table(name = "planes_nutricionales")
public class PlanNutricional {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Meta objetivo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_asignacion", nullable = false)
    private Instant fechaAsignacion;

    protected PlanNutricional() {
        // Requerido por JPA
    }

    public PlanNutricional(Usuario usuario, String nombre, Meta objetivo, String descripcion) {
        this.usuario = Objects.requireNonNull(usuario, "usuario es obligatorio");
        actualizar(nombre, objetivo, descripcion);
    }

    public void actualizar(String nombre, Meta objetivo, String descripcion) {
        this.nombre = Objects.requireNonNull(nombre, "nombre es obligatorio");
        this.objetivo = Objects.requireNonNull(objetivo, "objetivo es obligatorio");
        this.descripcion = descripcion;
        this.fechaAsignacion = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public Meta getObjetivo() {
        return objetivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Instant getFechaAsignacion() {
        return fechaAsignacion;
    }
}
