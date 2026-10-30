package com.nutrigo.backend.application.service;

import com.nutrigo.backend.infrastructure.adapter.in.web.dto.ExternalPostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {

    private static final String TEST_URL = "https://jsonplaceholder.typicode.com/posts/1";

    @Mock
    private RestTemplate restTemplate;

    private ExternalApiService externalApiService;

    @BeforeEach
    void setUp() {
        externalApiService = new ExternalApiService(restTemplate, TEST_URL);
    }

    @Test
    @DisplayName("Debe consumir la API externa exitosamente y retornar el DTO correspondiente")
    void debeObtenerInformacionExternaExitosamente() {
        // Arrange
        ExternalPostDto mockResponse = new ExternalPostDto(
                1L,
                1L,
                "Nutrición y Salud",
                "Contenido informativo sobre macronutrientes y micronutrientes."
        );

        when(restTemplate.getForObject(eq(TEST_URL), eq(ExternalPostDto.class)))
                .thenReturn(mockResponse);

        // Act
        ExternalPostDto resultado = externalApiService.obtenerInformacionExterna();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.userId()).isEqualTo(1L);
        assertThat(resultado.title()).isEqualTo("Nutrición y Salud");
        assertThat(resultado.body()).contains("macronutrientes");

        verify(restTemplate, times(1)).getForObject(eq(TEST_URL), eq(ExternalPostDto.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException cuando la API externa retorna cuerpo nulo")
    void debeLanzarExcepcionSiRespuestaEsNula() {
        // Arrange
        when(restTemplate.getForObject(eq(TEST_URL), eq(ExternalPostDto.class)))
                .thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> externalApiService.obtenerInformacionExterna())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nula");

        verify(restTemplate, times(1)).getForObject(eq(TEST_URL), eq(ExternalPostDto.class));
    }

    @Test
    @DisplayName("Debe propagar RestClientException y registrar el error cuando la petición HTTP falla")
    void debeLanzarYRegistrarErrorSiRestTemplateFalla() {
        // Arrange
        when(restTemplate.getForObject(eq(TEST_URL), eq(ExternalPostDto.class)))
                .thenThrow(new RestClientException("Connection timed out al conectar con el servidor externo"));

        // Act & Assert
        assertThatThrownBy(() -> externalApiService.obtenerInformacionExterna())
                .isInstanceOf(RestClientException.class)
                .hasMessageContaining("Connection timed out");

        verify(restTemplate, times(1)).getForObject(eq(TEST_URL), eq(ExternalPostDto.class));
    }
}

