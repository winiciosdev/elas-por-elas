package br.com.redeapoio.profissional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profissionais")
@Tag(name = "Profissionais", description = "Consulta e gestão de perfis de profissionais voluntárias.")
public class ProfissionalController {
    private final ProfissionalService service;

    public ProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar profissionais ativos", description = "Lista os perfis ativos de profissionais, com filtro opcional por cidade/UF.")
    public List<ProfissionalDtos.Response> listar(@RequestParam(required=false) String cidadeUf) {
        return service.listar(cidadeUf);
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar perfil profissional da usuária logada", description = "Retorna o perfil profissional da usuária autenticada, inclusive em análise ou em pausa.")
    public ProfissionalDtos.Response meu(Authentication auth) {
        return service.meu(auth.getName());
    }

    @PatchMapping("/me/disponibilidade")
    @Operation(summary = "Pausar ou reativar o perfil", description = "Define se o perfil aparece na busca pública (aceitando novos contatos) ou fica em pausa.")
    public ProfissionalDtos.Response disponibilidade(@Valid @RequestBody ProfissionalDtos.DisponibilidadeRequest r, Authentication auth) {
        return service.atualizarDisponibilidade(auth.getName(), r.disponivel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profissional por ID", description = "Retorna um perfil de profissional ativo pelo identificador.")
    public ProfissionalDtos.Response buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }

    @PostMapping("/me")
    @Operation(summary = "Criar perfil profissional da usuária logada", description = "Cria o perfil profissional da usuária autenticada para um tipo de serviço específico.")
    public ProfissionalDtos.Response criar(@Valid @RequestBody ProfissionalDtos.Request r,Authentication auth) {
        return service.criar(auth.getName(), r);
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar perfil profissional da usuária logada", description = "Atualiza os dados do perfil profissional da usuária autenticada.")
    public ProfissionalDtos.Response atualizar(@Valid @RequestBody ProfissionalDtos.Request r,Authentication auth) {
        return service.atualizar(auth.getName(), r);
    }

    @DeleteMapping("/me")
    @Operation(summary = "Desativar perfil profissional da usuária logada", description = "Desativa o perfil profissional da usuária autenticada sem excluir os dados históricos.")
    public void desativar(Authentication auth) {
        service.desativar(auth.getName());
    }

    @PostMapping("/{id}/verificacao") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verificar profissional", description = "Marca um perfil profissional como verificado pela administração.")
    public ProfissionalDtos.Response verificar(@PathVariable UUID id) {
        return service.verificar(id);
    }
}
