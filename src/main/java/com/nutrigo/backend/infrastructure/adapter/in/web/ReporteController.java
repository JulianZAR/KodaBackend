package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.ConsultarReportesUseCase;
import com.nutrigo.backend.application.port.in.GenerarReporteUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.ReporteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reportes", description = "Reportes nutricionales generados para un usuario")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final GenerarReporteUseCase generarReporteUseCase;
    private final ConsultarReportesUseCase consultarReportesUseCase;

    public ReporteController(ObtenerPerfilUseCase obtenerPerfilUseCase,
                              GenerarReporteUseCase generarReporteUseCase,
                              ConsultarReportesUseCase consultarReportesUseCase) {
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.generarReporteUseCase = generarReporteUseCase;
        this.consultarReportesUseCase = consultarReportesUseCase;
    }

    @PostMapping
    @Operation(summary = "Genera un nuevo reporte nutricional para el usuario autenticado")
    public ReporteResponse generarMiReporte(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerPerfilUseCase.obtenerOCrearPerfil(
                JwtIdentidad.keycloakUserId(jwt), JwtIdentidad.email(jwt), JwtIdentidad.nombre(jwt));
        return ReporteResponse.desde(generarReporteUseCase.generarReporte(usuario.getId()));
    }

    @GetMapping
    @Operation(summary = "Lista los reportes del usuario autenticado, del más reciente al más antiguo")
    public List<ReporteResponse> consultarMisReportes(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerPerfilUseCase.obtenerOCrearPerfil(
                JwtIdentidad.keycloakUserId(jwt), JwtIdentidad.email(jwt), JwtIdentidad.nombre(jwt));
        return consultarReportesUseCase.consultarReportes(usuario.getId()).stream()
                .map(ReporteResponse::desde)
                .toList();
    }

    @GetMapping("/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST')")
    @Operation(summary = "Lista los reportes de un usuario específico (solo ADMIN o NUTRITIONIST)")
    public List<ReporteResponse> consultarReportesDeUsuario(@PathVariable UUID usuarioId) {
        return consultarReportesUseCase.consultarReportes(usuarioId).stream()
                .map(ReporteResponse::desde)
                .toList();
    }
}
