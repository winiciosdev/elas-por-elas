package br.com.redeapoio.tiposervico;

import br.com.redeapoio.common.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tipos-servico")
@Tag(name = "Tipos de serviço", description = "Categorias de serviços disponíveis na plataforma.")
public class TipoServicoController {
    private final TipoServicoRepository repository;
    public TipoServicoController(TipoServicoRepository repository) { this.repository = repository; }

    @GetMapping
    @Operation(summary = "Listar tipos de serviço", description = "Retorna todas as categorias de serviço cadastradas para uso em cadastros e filtros.")
    public List<TipoServicoDtos.Response> listar() {
        return repository.findAll().stream().map(this::dto).toList();
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de serviço por ID", description = "Retorna um tipo de serviço específico pelo identificador informado.")
    public TipoServicoDtos.Response buscar(@PathVariable UUID id) {
        return dto(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado.")));
    }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar tipo de serviço", description = "Cria uma nova categoria de serviço. Requer perfil de administrador.")
    public TipoServicoDtos.Response criar(@Valid @RequestBody TipoServicoDtos.Request r) {
        return dto(repository.save(new TipoServico(r.nome(), r.descricao())));
    }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar tipo de serviço", description = "Atualiza os dados de uma categoria de serviço existente. Requer perfil de administrador.")
    public TipoServicoDtos.Response atualizar(@PathVariable UUID id, @Valid @RequestBody TipoServicoDtos.Request r) {
        var e = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
        e.atualizar(r.nome(), r.descricao()); return dto(repository.save(e));
    }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir tipo de serviço", description = "Remove um tipo de serviço do sistema. Requer perfil de administrador.")
    public void excluir(@PathVariable UUID id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Tipo de serviço não encontrado.");
        repository.deleteById(id);
    }
    private TipoServicoDtos.Response dto(TipoServico e) { return new TipoServicoDtos.Response(e.getId(), e.getNome(), e.getDescricao()); }
}
