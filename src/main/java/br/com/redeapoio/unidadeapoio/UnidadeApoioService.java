package br.com.redeapoio.unidadeapoio;

import br.com.redeapoio.common.ResourceNotFoundException;
import br.com.redeapoio.tiposervico.TipoServico;
import br.com.redeapoio.tiposervico.TipoServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UnidadeApoioService {
    private final UnidadeApoioRepository repository;
    private final TipoServicoRepository servicos;

    public UnidadeApoioService(UnidadeApoioRepository repository, TipoServicoRepository servicos) {
        this.repository = repository;
        this.servicos = servicos;
    }

    @Transactional(readOnly = true)
    public List<UnidadeApoioDtos.Response> listar(String cidade, String bairro) {
        List<UnidadeApoio> list;
        if (cidade != null && bairro != null) {
            list = repository.findByAtivaTrueAndCidadeContainingIgnoreCaseAndBairroContainingIgnoreCase(cidade, bairro);
        } else if (cidade != null) {
            list = repository.findByAtivaTrueAndCidadeContainingIgnoreCase(cidade);
        } else {
            list = repository.findByAtivaTrue();
        }
        return list.stream().map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public UnidadeApoioDtos.Response buscar(UUID id) {
        var entity = repository.findById(id).filter(UnidadeApoio::isAtiva)
            .orElseThrow(() -> new ResourceNotFoundException("Unidade de apoio não encontrada."));
        return dto(entity);
    }

    @Transactional
    public UnidadeApoioDtos.Response criar(UnidadeApoioDtos.Request request) {
        var entity = new UnidadeApoio(request.nome(), request.descricao(), request.cidade(), request.bairro(), request.endereco(), request.telefone(), request.site());
        entity.atualizar(request.nome(), request.descricao(), request.cidade(), request.bairro(), request.endereco(), request.telefone(), request.site(), resolver(request.tipoServicoIds()));
        return dto(repository.save(entity));
    }

    @Transactional
    public UnidadeApoioDtos.Response atualizar(UUID id, UnidadeApoioDtos.Request request) {
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada."));
        entity.atualizar(request.nome(), request.descricao(), request.cidade(), request.bairro(), request.endereco(), request.telefone(), request.site(), resolver(request.tipoServicoIds()));
        return dto(repository.save(entity));
    }

    @Transactional
    public void excluir(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Unidade não encontrada.");
        }
        repository.deleteById(id);
    }

    @Transactional
    public UnidadeApoioDtos.Response verificar(UUID id) {
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada."));
        entity.verificar();
        return dto(repository.save(entity));
    }

    private Set<TipoServico> resolver(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        var result = servicos.findAllById(ids);
        if (result.size() != new HashSet<>(ids).size()) {
            throw new IllegalArgumentException("Um ou mais tipos de serviço não existem.");
        }
        return new HashSet<>(result);
    }

    private UnidadeApoioDtos.Response dto(UnidadeApoio entity) {
        return new UnidadeApoioDtos.Response(entity.getId(), entity.getNome(), entity.getDescricao(), entity.getCidade(), entity.getBairro(),
            entity.getEndereco(), entity.getTelefone(), entity.getSite(), entity.isAtiva(), entity.getVerificadoEm(), entity.getCriadoEm(),
            entity.getServicos().stream().map(s -> new UnidadeApoioDtos.Servico(s.getId(), s.getNome())).toList());
    }
}
