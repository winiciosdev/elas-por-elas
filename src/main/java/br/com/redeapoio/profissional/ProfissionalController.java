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
