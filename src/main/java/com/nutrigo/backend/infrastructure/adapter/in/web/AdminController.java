package com.nutrigo.backend.infrastructure.adapter.in.web;

import com.nutrigo.backend.application.port.in.GestionarRolesUseCase;
import com.nutrigo.backend.domain.model.Rol;
import com.nutrigo.backend.infrastructure.adapter.in.web.dto.CuentaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administración", description = "Cuentas de Keycloak y roles elevados (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final GestionarRolesUseCase gestionarRolesUseCase;

    public AdminController(GestionarRolesUseCase gestionarRolesUseCase) {
        this.gestionarRolesUseCase = gestionarRolesUseCase;
    }

    @GetMapping
    @Operation(summary = "Lista las cuentas de Keycloak con sus roles")
    public List<CuentaResponse> listarCuentas() {
        return gestionarRolesUseCase.listarCuentas().stream().map(CuentaResponse::desde).toList();
    }

    @PutMapping("/{keycloakUserId}/roles/{rol}")
    @Operation(summary = "Otorga el rol NUTRITIONIST o ADMIN a una cuenta. "
            + "Se aplica cuando el usuario vuelve a iniciar sesión o renueva su token.")
    public ResponseEntity<Void> asignarRol(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable UUID keycloakUserId, @PathVariable Rol rol) {
        gestionarRolesUseCase.asignarRol(JwtIdentidad.keycloakUserId(jwt), keycloakUserId, rol);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{keycloakUserId}/roles/{rol}")
    @Operation(summary = "Retira el rol NUTRITIONIST o ADMIN de una cuenta (un ADMIN no puede quitarse el suyo)")
    public ResponseEntity<Void> quitarRol(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable UUID keycloakUserId, @PathVariable Rol rol) {
        gestionarRolesUseCase.quitarRol(JwtIdentidad.keycloakUserId(jwt), keycloakUserId, rol);
        return ResponseEntity.noContent().build();
    }
}
