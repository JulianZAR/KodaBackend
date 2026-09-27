package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.ConsultarProgresoUseCase;
import com.nutrigo.backend.application.port.in.RegistrarProgresoUseCase;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.ProgresoResponse;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.RegistrarProgresoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@Tag(name = "Progreso", description = "Historial de peso e IMC del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class ProgresoController {

    private final RegistrarProgresoUseCase registrarProgresoUseCase;
    private final ConsultarProgresoUseCase consultarProgresoUseCase;

    public ProgresoController(RegistrarProgresoUseCase registrarProgresoUseCase,
                               ConsultarProgresoUseCase consultarProgresoUseCase) {
        this.registrarProgresoUseCase = registrarProgresoUseCase;
        this.consultarProgresoUseCase = consultarProgresoUseCase;
    }

    @PostMapping
    @Operation(summary = "Registra un nuevo punto de progreso (peso) y calcula su IMC con la estatura actual")
    public ProgresoResponse registrar(@AuthenticationPrincipal Jwt jwt,
                                       @Valid @RequestBody RegistrarProgresoRequest request) {
        return ProgresoResponse.desde(registrarProgresoUseCase.registrarProgreso(
                JwtIdentidad.keycloakUserId(jwt), JwtIdentidad.email(jwt), JwtIdentidad.nombre(jwt),
                request.pesoKg()));
    }

    @GetMapping
    @Operation(summary = "Consulta el historial de progreso del usuario, del más reciente al más antiguo")
    public List<ProgresoResponse> consultarHistorial(@AuthenticationPrincipal Jwt jwt) {
        return consultarProgresoUseCase.consultarHistorial(
                        JwtIdentidad.keycloakUserId(jwt), JwtIdentidad.email(jwt), JwtIdentidad.nombre(jwt))
                .stream()
                .map(ProgresoResponse::desde)
                .toList();
    }
}
