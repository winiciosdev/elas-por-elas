package br.com.redeapoio.unidadeapoio;

import br.com.redeapoio.common.ResourceNotFoundException;
import br.com.redeapoio.tiposervico.TipoServicoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@RestController
@RequestMapping("/api/v1/unidades")
@Tag(name = "Unidades de apoio", description = "Consulta e gestão das unidades cadastradas na rede.")
public class UnidadeApoioController {
    private final UnidadeApoioRepository repository;
    private final TipoServicoRepository servicos;
    public UnidadeApoioController(UnidadeApoioRepository repository, TipoServicoRepository servicos) {
        this.repository=repository; this.servicos=servicos;
    }

    @GetMapping @Transactional(readOnly=true)
    @Operation(summary = "Listar unidades ativas", description = "Busca as unidades de apoio ativas, com possibilidade de filtrar por cidade e bairro.")
    public List<UnidadeApoioDtos.Response> listar(@RequestParam(required=false) String cidade,
                                                  @RequestParam(required=false) String bairro) {
        List<UnidadeApoio> list;
        if (cidade != null && bairro != null) list=repository.findByAtivaTrueAndCidadeContainingIgnoreCaseAndBairroContainingIgnoreCase(cidade,bairro);
        else if (cidade != null) list=repository.findByAtivaTrueAndCidadeContainingIgnoreCase(cidade);
        else list=repository.findByAtivaTrue();
        return list.stream().map(this::dto).toList();
    }
    @GetMapping("/{id}") @Transactional(readOnly=true)
    @Operation(summary = "Buscar unidade por ID", description = "Retorna uma unidade ativa pelo identificador, incluindo dados cadastrais e serviços vinculados.")
    public UnidadeApoioDtos.Response buscar(@PathVariable UUID id) {
        var e=repository.findById(id).filter(UnidadeApoio::isAtiva)
            .orElseThrow(()->new ResourceNotFoundException("Unidade de apoio não encontrada."));
        return dto(e);
    }
    @PostMapping @PreAuthorize("hasRole('ADMIN')") @Transactional
    @Operation(summary = "Criar unidade", description = "Cria uma nova unidade de apoio. Requer perfil de administrador.")
    public UnidadeApoioDtos.Response criar(@Valid @RequestBody UnidadeApoioDtos.Request r) {
        var e=new UnidadeApoio(r.nome(),r.descricao(),r.cidade(),r.bairro(),r.endereco(),r.telefone(),r.site());
        e.atualizar(r.nome(),r.descricao(),r.cidade(),r.bairro(),r.endereco(),r.telefone(),r.site(),resolver(r.tipoServicoIds()));
        return dto(repository.save(e));
    }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") @Transactional
    @Operation(summary = "Atualizar unidade", description = "Atualiza os dados de uma unidade existente. Requer perfil de administrador.")
    public UnidadeApoioDtos.Response atualizar(@PathVariable UUID id,@Valid @RequestBody UnidadeApoioDtos.Request r) {
        var e=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Unidade não encontrada."));
        e.atualizar(r.nome(),r.descricao(),r.cidade(),r.bairro(),r.endereco(),r.telefone(),r.site(),resolver(r.tipoServicoIds()));
        return dto(repository.save(e));
    }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir unidade", description = "Remove permanentemente uma unidade do sistema. Requer perfil de administrador.")
    public void excluir(@PathVariable UUID id) {
        if(!repository.existsById(id)) throw new ResourceNotFoundException("Unidade não encontrada.");
        repository.deleteById(id);
    }
    @PostMapping("/{id}/verificacao") @PreAuthorize("hasRole('ADMIN')") @Transactional
    @Operation(summary = "Validar unidade", description = "Marca uma unidade como verificada pela equipe administrativa.")
    public UnidadeApoioDtos.Response verificar(@PathVariable UUID id) {
        var e=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Unidade não encontrada."));
        e.verificar(); return dto(repository.save(e));
    }
    private Set<br.com.redeapoio.tiposervico.TipoServico> resolver(List<UUID> ids) {
        if(ids==null || ids.isEmpty()) return new HashSet<>();
        var result=servicos.findAllById(ids);
        if(result.size()!=new HashSet<>(ids).size()) throw new IllegalArgumentException("Um ou mais tipos de serviço não existem.");
        return new HashSet<>(result);
    }
    private UnidadeApoioDtos.Response dto(UnidadeApoio e) {
        return new UnidadeApoioDtos.Response(e.getId(),e.getNome(),e.getDescricao(),e.getCidade(),e.getBairro(),
            e.getEndereco(),e.getTelefone(),e.getSite(),e.isAtiva(),e.getVerificadoEm(),e.getCriadoEm(),
            e.getServicos().stream().map(s->new UnidadeApoioDtos.Servico(s.getId(),s.getNome())).toList());
    }
}
