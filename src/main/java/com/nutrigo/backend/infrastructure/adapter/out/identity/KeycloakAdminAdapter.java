package com.nutrigo.backend.infrastructure.adapter.out.identity;

import com.nutrigo.backend.application.port.out.ProveedorIdentidadPort;
import com.nutrigo.backend.domain.exception.IdentidadNoDisponibleException;
import com.nutrigo.backend.domain.exception.UsuarioNoEncontradoException;
import com.nutrigo.backend.domain.model.Rol;
import com.nutrigo.backend.domain.model.UsuarioIdentidad;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de salida hacia la Admin REST API de Keycloak. Se autentica con
 * la cuenta de servicio del cliente confidencial "nutrigo-backend"
 * (client_credentials), que solo tiene permisos de realm-management sobre
 * usuarios y roles del realm nutrigo.
 */
@Component
public class KeycloakAdminAdapter implements ProveedorIdentidadPort {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<Map<String, Object>> OBJETO =
            new ParameterizedTypeReference<>() {
            };
    private static final int MAX_USUARIOS = 1000;

    private final RestClient http;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    private String accessToken;
    private Instant expiraEn = Instant.EPOCH;

    public KeycloakAdminAdapter(RestClient.Builder builder,
                                @Value("${app.keycloak.admin.base-url}") String baseUrl,
                                @Value("${app.keycloak.admin.realm}") String realm,
                                @Value("${app.keycloak.admin.client-id}") String clientId,
                                @Value("${app.keycloak.admin.client-secret}") String clientSecret) {
        this.http = builder.baseUrl(baseUrl).build();
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public List<UsuarioIdentidad> listarCuentas() {
        try {
            List<Map<String, Object>> usuarios = get("/users?briefRepresentation=true&max=" + MAX_USUARIOS);
            Set<String> admins = idsConRol(Rol.ADMIN);
            Set<String> nutricionistas = idsConRol(Rol.NUTRITIONIST);

            return usuarios.stream()
                    .filter(u -> !texto(u, "username").startsWith("service-account-"))
                    .map(u -> {
                        String id = texto(u, "id");
                        Set<Rol> roles = EnumSet.of(Rol.USER); // rol por defecto del realm
                        if (nutricionistas.contains(id)) roles.add(Rol.NUTRITIONIST);
                        if (admins.contains(id)) roles.add(Rol.ADMIN);
                        String nombre = (texto(u, "firstName") + " " + texto(u, "lastName")).trim();
                        return new UsuarioIdentidad(
                                UUID.fromString(id),
                                texto(u, "username"),
                                texto(u, "email"),
                                nombre.isEmpty() ? texto(u, "username") : nombre,
                                Boolean.TRUE.equals(u.get("enabled")),
                                roles);
                    })
                    .toList();
        } catch (RestClientException ex) {
            throw new IdentidadNoDisponibleException("No se pudo consultar las cuentas en Keycloak.", ex);
        }
    }

    @Override
    public void asignarRol(UUID keycloakUserId, Rol rol) {
        cambiarRol(keycloakUserId, rol, HttpMethod.POST);
    }

    @Override
    public void quitarRol(UUID keycloakUserId, Rol rol) {
        cambiarRol(keycloakUserId, rol, HttpMethod.DELETE);
    }

    private void cambiarRol(UUID keycloakUserId, Rol rol, HttpMethod metodo) {
        try {
            Map<String, Object> representacionRol = http.get()
                    .uri(admin("/roles/" + rol.name()))
                    .headers(h -> h.setBearerAuth(token()))
                    .retrieve()
                    .body(OBJETO);

            http.method(metodo)
                    .uri(admin("/users/" + keycloakUserId + "/role-mappings/realm"))
                    .headers(h -> h.setBearerAuth(token()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of(representacionRol))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new UsuarioNoEncontradoException("No existe en Keycloak una cuenta con id " + keycloakUserId);
        } catch (RestClientException ex) {
            throw new IdentidadNoDisponibleException("No se pudo actualizar el rol en Keycloak.", ex);
        }
    }

    private Set<String> idsConRol(Rol rol) {
        return get("/roles/" + rol.name() + "/users?briefRepresentation=true&max=" + MAX_USUARIOS).stream()
                .map(u -> texto(u, "id"))
                .collect(Collectors.toSet());
    }

    private List<Map<String, Object>> get(String ruta) {
        List<Map<String, Object>> respuesta = http.get()
                .uri(admin(ruta))
                .headers(h -> h.setBearerAuth(token()))
                .retrieve()
                .body(LISTA);
        return respuesta != null ? respuesta : List.of();
    }

    private String admin(String ruta) {
        return "/admin/realms/" + realm + ruta;
    }

    /** Token de la cuenta de servicio, reutilizado hasta 30 s antes de expirar. */
    private synchronized String token() {
        if (accessToken != null && Instant.now().isBefore(expiraEn)) {
            return accessToken;
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        Map<String, Object> respuesta = http.post()
                .uri("/realms/" + realm + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(OBJETO);
        if (respuesta == null || respuesta.get("access_token") == null) {
            throw new IdentidadNoDisponibleException("Keycloak no devolvió un token de servicio.", null);
        }
        accessToken = (String) respuesta.get("access_token");
        long segundos = respuesta.get("expires_in") instanceof Number n ? n.longValue() : 60;
        expiraEn = Instant.now().plusSeconds(Math.max(segundos - 30, 5));
        return accessToken;
    }

    private static String texto(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        return valor != null ? valor.toString() : "";
    }
}
