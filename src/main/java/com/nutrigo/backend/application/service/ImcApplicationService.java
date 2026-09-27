package com.nutrigo.backend.application.service;

import com.nutrigo.backend.application.port.in.CalcularImcUseCase;
import com.nutrigo.backend.application.port.in.ConsultarImcActualUseCase;
import com.nutrigo.backend.application.port.out.UsuarioRepositoryPort;
import com.nutrigo.backend.domain.exception.PerfilIncompletoException;
import com.nutrigo.backend.domain.model.CategoriaImc;
import com.nutrigo.backend.domain.model.ResultadoImc;
import com.nutrigo.backend.domain.model.Usuario;
import com.nutrigo.backend.domain.service.CalculadoraIMC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ImcApplicationService implements CalcularImcUseCase, ConsultarImcActualUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ImcApplicationService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public ResultadoImc calcular(BigDecimal pesoKg, BigDecimal estaturaCm) {
        BigDecimal valor = CalculadoraIMC.calcular(pesoKg, estaturaCm);
        return new ResultadoImc(valor, CategoriaImc.desde(valor));
    }

    @Override
    public ResultadoImc consultarImcActual(UUID keycloakUserId) {
        Usuario usuario = usuarioRepositoryPort.buscarPorKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new PerfilIncompletoException(
                        "Aún no tienes un perfil registrado. Completa tu perfil antes de consultar tu IMC."));
        if (!usuario.tieneDatosFisicosCompletos()) {
            throw new PerfilIncompletoException("Tu perfil no tiene peso y estatura registrados todavía.");
        }
        return calcular(usuario.getPesoKg(), usuario.getEstaturaCm());
    }
}
