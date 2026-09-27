package com.nutrigo.backend.domain.model;

import com.nutrigo.backend.domain.service.CalculadoraIMC;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad JPA que ES el modelo de dominio (arquitectura hexagonal pragmática).
 * La identidad y las credenciales viven en Keycloak: aquí solo se guarda el
 * perfil nutricional, vinculado por {@code keycloakUserId}.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_user_id", nullable = false, unique = true)
    private UUID keycloakUserId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "estatura_cm", precision = 5, scale = 2)
    private BigDecimal estaturaCm;

    @Column(name = "peso_kg", precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Meta meta;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private Instant fechaRegistro;

    protected Usuario() {
        // Requerido por JPA
    }

    public Usuario(UUID keycloakUserId, String email, String nombre) {
        this.keycloakUserId = Objects.requireNonNull(keycloakUserId, "keycloakUserId es obligatorio");
        this.email = Objects.requireNonNull(email, "email es obligatorio");
        this.nombre = Objects.requireNonNull(nombre, "nombre es obligatorio");
        this.fechaRegistro = Instant.now();
    }

    public void sincronizarIdentidad(String email, String nombre) {
        if (email != null && !email.equals(this.email)) {
            this.email = email;
        }
        if (nombre != null && !nombre.equals(this.nombre)) {
            this.nombre = nombre;
        }
    }

    public void actualizarEstaturaYMeta(BigDecimal estaturaCm, Meta meta) {
        CalculadoraIMC.validarEstatura(estaturaCm);
        this.estaturaCm = estaturaCm;
        this.meta = meta;
    }

    public void actualizarPeso(BigDecimal pesoKg) {
        CalculadoraIMC.validarPeso(pesoKg);
        this.pesoKg = pesoKg;
    }

    public boolean tieneDatosFisicosCompletos() {
        return estaturaCm != null && pesoKg != null;
    }

    public UUID getId() {
        return id;
    }

    public UUID getKeycloakUserId() {
        return keycloakUserId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getEstaturaCm() {
        return estaturaCm;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public Meta getMeta() {
        return meta;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }
}
