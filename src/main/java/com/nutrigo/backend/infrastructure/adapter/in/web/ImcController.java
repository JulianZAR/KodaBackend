package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.CalcularImcUseCase;
import com.nutrigo.backend.application.port.in.ConsultarImcActualUseCase;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.CalcularImcRequest;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.ImcResponse;
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

@RestController
@RequestMapping("/api/imc")
@Tag(name = "IMC", description = "Cálculo del índice de masa corporal")
@SecurityRequirement(name = "bearerAuth")
public class ImcController {

    private final CalcularImcUseCase calcularImcUseCase;
    private final ConsultarImcActualUseCase consultarImcActualUseCase;

    public ImcController(CalcularImcUseCase calcularImcUseCase,
                          ConsultarImcActualUseCase consultarImcActualUseCase) {
        this.calcularImcUseCase = calcularImcUseCase;
        this.consultarImcActualUseCase = consultarImcActualUseCase;
    }

    @PostMapping
    @Operation(summary = "Calcula el IMC a partir de un peso y estatura dados, sin usar el perfil guardado")
    public ImcResponse calcular(@Valid @RequestBody CalcularImcRequest request) {
        return ImcResponse.desde(calcularImcUseCase.calcular(request.pesoKg(), request.estaturaCm()));
    }

    @GetMapping("/me")
    @Operation(summary = "Calcula el IMC del usuario autenticado usando su peso y estatura guardados")
    public ImcResponse miImcActual(@AuthenticationPrincipal Jwt jwt) {
        return ImcResponse.desde(consultarImcActualUseCase.consultarImcActual(JwtIdentidad.keycloakUserId(jwt)));
    }
}
