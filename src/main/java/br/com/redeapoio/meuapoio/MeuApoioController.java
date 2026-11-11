package br.com.redeapoio.meuapoio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meu-apoio")
@Tag(name = "Meu Apoio", description = "Lista e gerencia as unidades e profissionais salvos pela usuária.")
public class MeuApoioController {
    private final MeuApoioService service;

    public MeuApoioController(MeuApoioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar itens salvos", description = "Retorna todas as unidades e profissionais marcados como favoritos pela usuária autenticada.")
    public List<MeuApoioDtos.Response> listar(Authentication auth) {
        return service.listar(auth.getName());
    }

    @PostMapping
    @Operation(summary = "Salvar unidade ou profissional", description = "Adiciona uma unidade ou profissional à lista de favoritos da usuária autenticada.")
    public MeuApoioDtos.Response salvar(@Valid @RequestBody MeuApoioDtos.Request r,Authentication auth) {
        return service.salvar(r, auth.getName());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover item salvo", description = "Remove uma unidade ou profissional da lista de favoritos da usuária autenticada.")
    public void remover(@PathVariable UUID id,Authentication auth) {
        service.remover(id, auth.getName());
    }
}
