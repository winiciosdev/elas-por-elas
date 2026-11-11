package br.com.redeapoio.unidadeapoio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unidades")
@Tag(name = "Unidades de apoio", description = "Consulta e gestão das unidades cadastradas na rede.")
public class UnidadeApoioController {
    private final UnidadeApoioService service;

    public UnidadeApoioController(UnidadeApoioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar unidades ativas", description = "Busca as unidades de apoio ativas, com possibilidade de filtrar por cidade e bairro.")
    public List<UnidadeApoioDtos.Response> listar(@RequestParam(required=false) String cidade,
                                                  @RequestParam(required=false) String bairro) {
        return service.listar(cidade, bairro);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar unidade por ID", description = "Retorna uma unidade ativa pelo identificador, incluindo dados cadastrais e serviços vinculados.")
    public UnidadeApoioDtos.Response buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }

    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar unidade", description = "Cria uma nova unidade de apoio. Requer perfil de administrador.")
    public UnidadeApoioDtos.Response criar(@Valid @RequestBody UnidadeApoioDtos.Request r) {
        return service.criar(r);
    }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar unidade", description = "Atualiza os dados de uma unidade existente. Requer perfil de administrador.")
    public UnidadeApoioDtos.Response atualizar(@PathVariable UUID id,@Valid @RequestBody UnidadeApoioDtos.Request r) {
        return service.atualizar(id, r);
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir unidade", description = "Remove permanentemente uma unidade do sistema. Requer perfil de administrador.")
    public void excluir(@PathVariable UUID id) {
        service.excluir(id);
    }

    @PostMapping("/{id}/verificacao") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Validar unidade", description = "Marca uma unidade como verificada pela equipe administrativa.")
    public UnidadeApoioDtos.Response verificar(@PathVariable UUID id) {
        return service.verificar(id);
    }
}
