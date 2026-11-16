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
 * Aviso para un {@link Usuario} sobre algo que otra persona hizo en su cuenta
 * (plan asignado, reporte generado, cambio de rol). Los textos se construyen
 * aquí para que todo el sistema use los mismos mensajes.
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoNotificacion tipo;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private boolean leida;

    @Column(nullable = false, updatable = false)
    private Instant fecha;

    protected Notificacion() {
        // Requerido por JPA
    }

    public Notificacion(Usuario usuario, TipoNotificacion tipo, String titulo, String mensaje) {
        this.usuario = Objects.requireNonNull(usuario, "usuario es obligatorio");
        this.tipo = Objects.requireNonNull(tipo, "tipo es obligatorio");
        this.titulo = Objects.requireNonNull(titulo, "titulo es obligatorio");
        this.mensaje = Objects.requireNonNull(mensaje, "mensaje es obligatorio");
        this.leida = false;
        this.fecha = Instant.now();
    }

    public static Notificacion planAsignado(PlanNutricional plan, boolean esNuevo) {
        return esNuevo
                ? new Notificacion(plan.getUsuario(), TipoNotificacion.PLAN_ASIGNADO,
                        "Tienes un plan nutricional nuevo",
                        "Tu nutricionista te asignó el plan \"" + plan.getNombre() + "\".")
                : new Notificacion(plan.getUsuario(), TipoNotificacion.PLAN_ACTUALIZADO,
                        "Tu plan nutricional se actualizó",
                        "Tu nutricionista hizo cambios en tu plan \"" + plan.getNombre() + "\".");
    }

    public static Notificacion reporteGenerado(Usuario usuario) {
        return new Notificacion(usuario, TipoNotificacion.REPORTE_GENERADO,
                "Nuevo reporte disponible",
                "Tu nutricionista generó un reporte nutricional con tus datos actuales.");
    }

    public static Notificacion rolAsignado(Usuario usuario, Rol rol) {
        return new Notificacion(usuario, TipoNotificacion.ROL_ASIGNADO,
                "Ahora eres " + nombreRol(rol),
                "Un administrador te otorgó el rol de " + nombreRol(rol)
                        + ". Vuelve a iniciar sesión para ver las nuevas opciones.");
    }

    public static Notificacion rolRetirado(Usuario usuario, Rol rol) {
        return new Notificacion(usuario, TipoNotificacion.ROL_RETIRADO,
                "Ya no eres " + nombreRol(rol),
                "Un administrador retiró tu rol de " + nombreRol(rol) + ".");
    }

    private static String nombreRol(Rol rol) {
        return switch (rol) {
            case ADMIN -> "administrador";
            case NUTRITIONIST -> "nutricionista";
            case USER -> "usuario";
        };
    }

    public void marcarLeida() {
        this.leida = true;
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isLeida() {
        return leida;
    }

    public Instant getFecha() {
        return fecha;
    }
}
