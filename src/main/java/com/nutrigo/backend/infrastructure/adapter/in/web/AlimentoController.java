package com.nutrigo.backend.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Controlador para integración con API externa OpenFoodFacts.
 * Permite buscar información nutricional de alimentos mediante código de barras.
 */
@RestController
@RequestMapping("/api/alimentos")
@Tag(name = "Alimentos Externos", description = "Integración con OpenFoodFacts API para información nutricional")
@SecurityRequirement(name = "bearerAuth")
public class AlimentoController {

    private final RestTemplate restTemplate;
    private static final String OPENFOODFACTS_API_URL = "https://world.openfoodfacts.org/api/v0/product/";

    public AlimentoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Consulta información nutricional de un alimento desde OpenFoodFacts.
     * 
     * @param barcode Código de barras del producto (EAN-13, UPC, etc.)
     * @return JSON con información del producto desde OpenFoodFacts
     * 
     * Ejemplo de uso:
     * GET /api/alimentos/externo/737628064502
     * GET /api/alimentos/externo/3017620422003
     */
    @GetMapping("/externo/{barcode}")
    @Operation(
        summary = "Obtiene información nutricional de un alimento por código de barras",
        description = "Consume la API pública de OpenFoodFacts para obtener datos nutricionales completos del producto. " +
                      "Útil para registrar alimentos en planes nutricionales."
    )
    public ResponseEntity<String> obtenerAlimentoPorBarcode(@PathVariable String barcode) {
        try {
            // Construir URL completa
            String url = OPENFOODFACTS_API_URL + barcode + ".json";
            
            // Consumir API externa
            String response = restTemplate.getForObject(url, String.class);
            
            // Devolver respuesta directa de la API externa
            return ResponseEntity.ok(response);
            
        } catch (HttpClientErrorException.NotFound e) {
            // Producto no encontrado en OpenFoodFacts
            String errorJson = String.format(
                "{\"status\":0,\"status_verbose\":\"product not found\",\"code\":\"%s\"}", 
                barcode
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorJson);
            
        } catch (Exception e) {
            // Error general al consumir la API externa
            String errorJson = String.format(
                "{\"status\":0,\"status_verbose\":\"error connecting to external API\",\"error\":\"%s\"}", 
                e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorJson);
        }
    }
}
