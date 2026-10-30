package com.nutrigo.backend.application.service;

import com.nutrigo.backend.infrastructure.adapter.in.web.dto.ExternalPostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiService {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiService.class);

    private final RestTemplate restTemplate;
    private final String externalApiUrl;

    public ExternalApiService(RestTemplate restTemplate,
                              @Value("${app.external.api-url:https://jsonplaceholder.typicode.com/posts/1}") String externalApiUrl) {
        this.restTemplate = restTemplate;
        this.externalApiUrl = externalApiUrl;
    }

    /**
     * Consume un servicio REST externo con trazabilidad de logs explícita.
     *
     * @return ExternalPostDto con la información obtenida.
     */
    public ExternalPostDto obtenerInformacionExterna() {
        log.info("Iniciando peticion a la API externa en la URL: {}", externalApiUrl);

        try {
            ExternalPostDto response = restTemplate.getForObject(externalApiUrl, ExternalPostDto.class);

            if (response != null) {
                log.info("Respuesta exitosa recibida de la API externa: id={}, title='{}'", response.id(), response.title());
                return response;
            } else {
                log.error("La API externa en {} retorno una respuesta nula", externalApiUrl);
                throw new IllegalStateException("Respuesta nula recibida de la API externa");
            }
        } catch (RestClientException ex) {
            log.error("Error al consumir la API externa en {}: {}", externalApiUrl, ex.getMessage(), ex);
            throw ex;
        }
    }

    public String getExternalApiUrl() {
        return externalApiUrl;
    }
}

