package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.ConsultarPacientesUseCase;
import com.nutrigo.backend.application.port.out.PlanNutricionalRepositoryPort;
import com.nutrigo.backend.application.port.out.RegistroProgresoRepositoryPort;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.UsuarioNoEncontradoException;
import com.nutrigo.backend.domain.model.CategoriaImc;
import com.nutrigo.backend.domain.model.PlanNutricional;
import com.nutrigo.backend.domain.model.RegistroProgreso;
import com.nutrigo.backend.domain.model.ResultadoImc;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.domain.service.CalculadoraIMC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PacientesApplicationService implements ConsultarPacientesUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PlanNutricionalRepositoryPort planNutricionalRepositoryPort;
    private final RegistroProgresoRepositoryPort registroProgresoRepositoryPort;

    public PacientesApplicationService(UsuarioRepositoryPort usuarioRepositoryPort,
                                       PlanNutricionalRepositoryPort planNutricionalRepositoryPort,
                                       RegistroProgresoRepositoryPort registroProgresoRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.planNutricionalRepositoryPort = planNutricionalRepositoryPort;
        this.registroProgresoRepositoryPort = registroProgresoRepositoryPort;
    }

    @Override
    public List<ResumenPaciente> listarPacientes() {
        // Una sola consulta de planes en lugar de una por usuario
        Map<UUID, PlanNutricional> planesPorUsuario = planNutricionalRepositoryPort.listarTodos().stream()
                .collect(Collectors.toMap(plan -> plan.getUsuario().getId(), Function.identity()));

        return usuarioRepositoryPort.listarTodos().stream()
                .map(usuario -> resumir(usuario, planesPorUsuario.get(usuario.getId())))
                .toList();
    }

    @Override
    public ResumenPaciente obtenerPaciente(UUID usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        return resumir(usuario, planNutricionalRepositoryPort.buscarPorUsuarioId(usuarioId).orElse(null));
    }

    @Override
    public List<RegistroProgreso> consultarHistorialDePaciente(UUID usuarioId) {
        buscarUsuario(usuarioId);
        return registroProgresoRepositoryPort.buscarPorUsuarioOrdenadoDesc(usuarioId);
    }

    private Usuario buscarUsuario(UUID usuarioId) {
        return usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No existe un usuario con id " + usuarioId));
    }

    private ResumenPaciente resumir(Usuario usuario, PlanNutricional plan) {
        ResultadoImc imc = null;
        if (usuario.tieneDatosFisicosCompletos()) {
            BigDecimal valor = CalculadoraIMC.calcular(usuario.getPesoKg(), usuario.getEstaturaCm());
            imc = new ResultadoImc(valor, CategoriaImc.desde(valor));
        }
        return new ResumenPaciente(usuario, imc, plan);
    }
}
