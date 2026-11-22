package br.com.redeapoio.meuapoio;

import br.com.redeapoio.common.ResourceNotFoundException;
import br.com.redeapoio.profissional.ProfissionalRepository;
import br.com.redeapoio.unidadeapoio.UnidadeApoioRepository;
import br.com.redeapoio.usuaria.UsuariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MeuApoioService {
    private final MeuApoioRepository repository;
    private final UsuariaRepository usuarias;
    private final UnidadeApoioRepository unidades;
    private final ProfissionalRepository profissionais;

    public MeuApoioService(MeuApoioRepository repository, UsuariaRepository usuarias, UnidadeApoioRepository unidades, ProfissionalRepository profissionais) {
        this.repository = repository;
        this.usuarias = usuarias;
        this.unidades = unidades;
        this.profissionais = profissionais;
    }

    @Transactional(readOnly = true)
    public List<MeuApoioDtos.Response> listar(String emailAutenticado) {
        return repository.findByUsuariaEmailIgnoreCaseOrderByCriadoEmDesc(emailAutenticado).stream().map(this::dto).toList();
    }

    @Transactional
    public MeuApoioDtos.Response salvar(MeuApoioDtos.Request request, String emailAutenticado) {
        var usuaria = usuarias.findByEmailIgnoreCase(emailAutenticado).orElseThrow(() -> new ResourceNotFoundException("Usuária não encontrada."));
        if (request.unidadeId() != null) {
            if (repository.existsByUsuariaEmailIgnoreCaseAndUnidadeId(emailAutenticado, request.unidadeId())) {
                throw new IllegalArgumentException("Esta unidade já está salva nos seus apoios.");
            }
            var unidade = unidades.findById(request.unidadeId()).orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada."));
            return dto(repository.save(new MeuApoio(usuaria, unidade, null)));
        }
        if (repository.existsByUsuariaEmailIgnoreCaseAndProfissionalId(emailAutenticado, request.profissionalId())) {
            throw new IllegalArgumentException("Esta profissional já está salva nos seus apoios.");
        }
        var profissional = profissionais.findById(request.profissionalId()).orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrada."));
        return dto(repository.save(new MeuApoio(usuaria, null, profissional)));
    }

    @Transactional
    public void remover(UUID id, String emailAutenticado) {
        var item = repository.findByIdAndUsuariaEmailIgnoreCase(id, emailAutenticado)
            .orElseThrow(() -> new ResourceNotFoundException("Favorito não encontrado."));
        repository.delete(item);
    }

    private MeuApoioDtos.Response dto(MeuApoio entity) {
        return new MeuApoioDtos.Response(entity.getId(),
            entity.getUnidade() == null ? null : entity.getUnidade().getId(),
            entity.getUnidade() == null ? null : entity.getUnidade().getNome(),
            entity.getProfissional() == null ? null : entity.getProfissional().getId(),
            entity.getProfissional() == null ? null : entity.getProfissional().getUsuaria().getNome(),
            entity.getCriadoEm());
    }
}
