package br.com.redeapoio.tiposervico;

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
    private final TipoServicoService service;

    public TipoServicoController(TipoServicoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar tipos de serviço", description = "Retorna todas as categorias de serviço cadastradas para uso em cadastros e filtros.")
    public List<TipoServicoDtos.Response> listar() {
        return service.listar();
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de serviço por ID", description = "Retorna um tipo de serviço específico pelo identificador informado.")
    public TipoServicoDtos.Response buscar(@PathVariable UUID id) {
        return service.buscar(id);
    }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar tipo de serviço", description = "Cria uma nova categoria de serviço. Requer perfil de administrador.")
    public TipoServicoDtos.Response criar(@Valid @RequestBody TipoServicoDtos.Request r) {
        return service.criar(r);
    }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar tipo de serviço", description = "Atualiza os dados de uma categoria de serviço existente. Requer perfil de administrador.")
    public TipoServicoDtos.Response atualizar(@PathVariable UUID id, @Valid @RequestBody TipoServicoDtos.Request r) {
        return service.atualizar(id, r);
    }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir tipo de serviço", description = "Remove um tipo de serviço do sistema. Requer perfil de administrador.")
    public void excluir(@PathVariable UUID id) {
        service.excluir(id);
    }
}
