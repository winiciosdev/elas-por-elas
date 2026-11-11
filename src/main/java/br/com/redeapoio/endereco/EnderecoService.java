package br.com.redeapoio.endereco;

import br.com.redeapoio.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EnderecoService {
    private final EnderecoRepository repository;

    public EnderecoService(EnderecoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EnderecoDtos.Response> listar() {
        return repository.findAll().stream().map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public EnderecoDtos.Response buscar(UUID id) {
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado."));
        return dto(entity);
    }

    @Transactional
    public EnderecoDtos.Response criar(EnderecoDtos.Request request) {
        return dto(repository.save(map(request, new Endereco())));
    }

    @Transactional
    public EnderecoDtos.Response atualizar(UUID id, EnderecoDtos.Request request) {
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado."));
        return dto(repository.save(map(request, entity)));
    }

    private Endereco map(EnderecoDtos.Request request, Endereco entity) {
        entity.setCep(request.cep());
        entity.setLogradouro(request.logradouro());
        entity.setNumero(request.numero());
        entity.setComplemento(request.complemento());
        entity.setBairro(request.bairro());
        entity.setCidade(request.cidade());
        entity.setUf(request.uf());
        entity.setLatitude(request.latitude());
        entity.setLongitude(request.longitude());
        return entity;
    }

    private EnderecoDtos.Response dto(Endereco entity) {
        return new EnderecoDtos.Response(entity.getId(), entity.getCep(), entity.getLogradouro(), entity.getNumero(),
            entity.getComplemento(), entity.getBairro(), entity.getCidade(), entity.getUf(), entity.getLatitude(), entity.getLongitude());
    }
}
