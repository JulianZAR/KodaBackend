package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.ActualizarPerfilUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.ActualizarPerfilRequest;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.PerfilResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Perfil nutricional del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final ActualizarPerfilUseCase actualizarPerfilUseCase;

    public UsuarioController(ObtenerPerfilUseCase obtenerPerfilUseCase,
                              ActualizarPerfilUseCase actualizarPerfilUseCase) {
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.actualizarPerfilUseCase = actualizarPerfilUseCase;
    }

    @GetMapping("/me")
    @Operation(summary = "Obtiene el perfil del usuario autenticado, aprovisionándolo si es su primera petición")
    public PerfilResponse obtenerMiPerfil(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId(jwt), email(jwt), nombre(jwt));
        return PerfilResponse.desde(usuario);
    }

    @PutMapping("/me")
    @Operation(summary = "Completa o actualiza estatura, peso y meta del usuario autenticado")
    public PerfilResponse actualizarMiPerfil(@AuthenticationPrincipal Jwt jwt,
                                              @Valid @RequestBody ActualizarPerfilRequest request) {
        Usuario usuario = actualizarPerfilUseCase.actualizarPerfil(
                keycloakUserId(jwt), email(jwt), nombre(jwt),
                request.estaturaCm(), request.pesoKg(), request.meta());
        return PerfilResponse.desde(usuario);
    }

    private UUID keycloakUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    private String email(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }

    private String nombre(Jwt jwt) {
        String nombreCompleto = jwt.getClaimAsString("name");
        if (nombreCompleto != null && !nombreCompleto.isBlank()) {
            return nombreCompleto;
        }
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        return preferredUsername != null ? preferredUsername : email(jwt);
    }
}
