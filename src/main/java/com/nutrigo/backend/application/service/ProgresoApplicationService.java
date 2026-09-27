package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.ConsultarProgresoUseCase;
import com.nutrigo.backend.application.port.in.ObtenerPerfilUseCase;
import com.nutrigo.backend.application.port.in.RegistrarProgresoUseCase;
import com.nutrigo.backend.application.port.out.RegistroProgresoRepositoryPort;
import com.nutrigo.backend.domain.exception.PerfilIncompletoException;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.domain.service.CalculadoraIMC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProgresoApplicationService implements RegistrarProgresoUseCase, ConsultarProgresoUseCase {

    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final RegistroProgresoRepositoryPort registroProgresoRepositoryPort;

    public ProgresoApplicationService(ObtenerPerfilUseCase obtenerPerfilUseCase,
                                       RegistroProgresoRepositoryPort registroProgresoRepositoryPort) {
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.registroProgresoRepositoryPort = registroProgresoRepositoryPort;
    }

    @Override
    public RegistroProgreso registrarProgreso(UUID keycloakUserId, String email, String nombre, BigDecimal pesoKg) {
        Usuario usuario = obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId, email, nombre);
        if (usuario.getEstaturaCm() == null) {
            throw new PerfilIncompletoException("Registra tu estatura en tu perfil antes de registrar tu peso.");
        }
        BigDecimal imc = CalculadoraIMC.calcular(pesoKg, usuario.getEstaturaCm());
        usuario.actualizarPeso(pesoKg);
        RegistroProgreso registro = new RegistroProgreso(usuario, pesoKg, imc);
        return registroProgresoRepositoryPort.guardar(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroProgreso> consultarHistorial(UUID keycloakUserId, String email, String nombre) {
        Usuario usuario = obtenerPerfilUseCase.obtenerOCrearPerfil(keycloakUserId, email, nombre);
        return registroProgresoRepositoryPort.buscarPorUsuarioOrdenadoDesc(usuario.getId());
    }
}
