package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.AsignarPlanUseCase;
import com.nutrigo.backend.application.port.in.ConsultarPlanUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.AsignarPlanRequest;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.PlanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
@Tag(name = "Planes", description = "Planes nutricionales asignados por nutricionistas o administradores")
@SecurityRequirement(name = "bearerAuth")
public class PlanController {

    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final AsignarPlanUseCase asignarPlanUseCase;
    private final ConsultarPlanUseCase consultarPlanUseCase;

    public PlanController(ObtenerPerfilUseCase obtenerPerfilUseCase,
                           AsignarPlanUseCase asignarPlanUseCase,
                           ConsultarPlanUseCase consultarPlanUseCase) {
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.asignarPlanUseCase = asignarPlanUseCase;
        this.consultarPlanUseCase = consultarPlanUseCase;
    }

    @GetMapping("/me")
    @Operation(summary = "Consulta el plan nutricional vigente del usuario autenticado")
    public ResponseEntity<PlanResponse> obtenerMiPlan(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerPerfilUseCase.obtenerOCrearPerfil(
                JwtIdentidad.keycloakUserId(jwt), JwtIdentidad.email(jwt), JwtIdentidad.nombre(jwt));
        return consultarPlanUseCase.consultarPlan(usuario.getId())
                .map(PlanResponse::desde)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    @Operation(summary = "Consulta el plan nutricional de un usuario específico (solo ADMIN o NUTRITIONIST)")
    public ResponseEntity<PlanResponse> obtenerPlanDeUsuario(@PathVariable UUID usuarioId) {
        return consultarPlanUseCase.consultarPlan(usuarioId)
                .map(PlanResponse::desde)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    @Operation(summary = "Asigna o reemplaza el plan nutricional vigente de un usuario (solo ADMIN o NUTRITIONIST)")
    public PlanResponse asignarPlan(@PathVariable UUID usuarioId, @Valid @RequestBody AsignarPlanRequest request) {
        return PlanResponse.desde(asignarPlanUseCase.asignarPlan(
                usuarioId, request.nombre(), request.objetivo(), request.descripcion()));
    }
}
