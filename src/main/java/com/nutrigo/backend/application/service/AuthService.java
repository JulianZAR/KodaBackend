package com.nutrigo.backend.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public record UserPrincipal(
            UUID keycloakUserId,
            String email,
            String name,
            List<String> roles
    ) {}

    /**
     * Extrae y valida la identidad y roles contenidos en un token JWT de Keycloak.
     *
     * @param jwt Token emitido por el servidor de autorización
     * @return UserPrincipal con la información decodificada
     */
    public UserPrincipal autenticarYExtraerPrincipal(Jwt jwt) {
        if (jwt == null) {
            log.error("Fallo de autenticacion: Token JWT es nulo");
            throw new IllegalArgumentException("El token JWT no puede ser nulo");
        }

        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            log.error("Fallo de autenticacion: Claim 'sub' no encontrado en el token");
            throw new IllegalArgumentException("El token no contiene un identificador 'sub' valido");
        }

        UUID keycloakUserId = UUID.fromString(subject);
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        if (name == null || name.isBlank()) {
            name = jwt.getClaimAsString("preferred_username");
        }

        List<String> roles = extraerRoles(jwt);

        log.info("Usuario autenticado exitosamente: id={}, email={}, roles={}", keycloakUserId, email, roles);

        return new UserPrincipal(keycloakUserId, email != null ? email : "", name != null ? name : "Usuario", roles);
    }

    /**
     * Verifica si el usuario autenticado posee un rol específico.
     */
    public boolean tieneRol(Jwt jwt, String rolBuscado) {
        if (jwt == null || rolBuscado == null) {
            return false;
        }
        List<String> roles = extraerRoles(jwt);
        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(rolBuscado));
    }

    private List<String> extraerRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> rawRoles)) {
            return List.of();
        }

        return rawRoles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
    }
}

