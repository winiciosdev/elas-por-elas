package br.com.redeapoio.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Sistema", description = "Endpoints de infraestrutura e diagnóstico do serviço.")
public class HealthController {
    @GetMapping
    @Operation(summary = "Verificar status da API", description = "Retorna um simples indicador de que a aplicação está respondendo corretamente.")
    public Map<String, String> health() {
        return Map.of("status", "UP", "application", "rede-apoio");
    }
}
