package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.GestionarNotificacionesUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.NotificacionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones", description = "Avisos del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class NotificacionController {

    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final GestionarNotificacionesUseCase gestionarNotificacionesUseCase;

    public NotificacionController(ObtenerPerfilUseCase obtenerPerfilUseCase,
                                  GestionarNotificacionesUseCase gestionarNotificacionesUseCase) {
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.gestionarNotificacionesUseCase = gestionarNotificacionesUseCase;
    }

    @GetMapping
    @Operation(summary = "Lista las 50 notificaciones más recientes del usuario autenticado")
    public List<NotificacionResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return gestionarNotificacionesUseCase.listar(usuarioId(jwt)).stream()
                .map(NotificacionResponse::desde)
                .toList();
    }

    @PatchMapping("/{notificacionId}/read")
    @Operation(summary = "Marca una notificación propia como leída")
    public ResponseEntity<Void> marcarLeida(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID notificacionId) {
        gestionarNotificacionesUseCase.marcarLeida(usuarioId(jwt), notificacionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    @Operation(summary = "Marca todas las notificaciones del usuario como leídas")
    public ResponseEntity<Void> marcarTodasLeidas(@AuthenticationPrincipal Jwt jwt) {
        gestionarNotificacionesUseCase.marcarTodasLeidas(usuarioId(jwt));
        return ResponseEntity.noContent().build();
    }

    private UUID usuarioId(Jwt jwt) {
        return obtenerPerfilUseCase.obtenerOCrearPerfil(
                JwtIdentidad.keycloakUserId(jwt), JwtIdentidad.email(jwt), JwtIdentidad.nombre(jwt)).getId();
    }
}
