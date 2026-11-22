package br.com.redeapoio.tiposervico;

import br.com.redeapoio.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TipoServicoService {
    private final TipoServicoRepository repository;

    public TipoServicoService(TipoServicoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TipoServicoDtos.Response> listar() {
        return repository.findAll().stream().map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public TipoServicoDtos.Response buscar(UUID id) {
        return dto(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado.")));
    }

    @Transactional
    public TipoServicoDtos.Response criar(TipoServicoDtos.Request request) {
        return dto(repository.save(new TipoServico(request.nome(), request.codigo(), request.descricao())));
    }

    @Transactional
    public TipoServicoDtos.Response atualizar(UUID id, TipoServicoDtos.Request request) {
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
        entity.atualizar(request.nome(), request.codigo(), request.descricao());
        return dto(repository.save(entity));
    }

    @Transactional
    public void excluir(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de serviço não encontrado.");
        }
        repository.deleteById(id);
    }

    private TipoServicoDtos.Response dto(TipoServico entity) {
        return new TipoServicoDtos.Response(entity.getId(), entity.getCodigo(), entity.getNome(), entity.getDescricao());
    }
}
