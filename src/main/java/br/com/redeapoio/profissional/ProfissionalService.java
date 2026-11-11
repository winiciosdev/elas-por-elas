package br.com.redeapoio.profissional;

import br.com.redeapoio.common.ResourceNotFoundException;
import br.com.redeapoio.tiposervico.TipoServicoRepository;
import br.com.redeapoio.unidadeapoio.UnidadeApoio;
import br.com.redeapoio.unidadeapoio.UnidadeApoioRepository;
import br.com.redeapoio.usuaria.UsuariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfissionalService {
    private final ProfissionalRepository repository;
    private final UsuariaRepository usuarias;
    private final TipoServicoRepository tipos;
    private final UnidadeApoioRepository unidades;

    public ProfissionalService(ProfissionalRepository repository, UsuariaRepository usuarias, TipoServicoRepository tipos, UnidadeApoioRepository unidades) {
        this.repository = repository;
        this.usuarias = usuarias;
        this.tipos = tipos;
        this.unidades = unidades;
    }

    @Transactional(readOnly = true)
    public List<ProfissionalDtos.Response> listar(String cidadeUf) {
        var list = cidadeUf == null ? repository.findByAtivaTrue() : repository.findByAtivaTrueAndCidadeUfContainingIgnoreCase(cidadeUf);
        return list.stream().map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public ProfissionalDtos.Response buscar(UUID id) {
        var entity = repository.findById(id).filter(ProfissionalVoluntaria::isAtiva)
            .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrada."));
        return dto(entity);
    }

    @Transactional
    public ProfissionalDtos.Response criar(String emailAutenticado, ProfissionalDtos.Request request) {
        if (repository.findByUsuariaEmailIgnoreCase(emailAutenticado).isPresent()) {
            throw new IllegalArgumentException("Esta usuária já possui perfil profissional.");
        }
        var usuaria = usuarias.findByEmailIgnoreCase(emailAutenticado).orElseThrow(() -> new ResourceNotFoundException("Usuária não encontrada."));
        var tipo = tipos.findById(request.tipoServicoId()).orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
        var unidadesVinculadas = resolverUnidades(request.unidadeIds());
        var entity = new ProfissionalVoluntaria(usuaria, tipo, request.registroProfissional(), request.biografia(), request.cidadeUf(), unidadesVinculadas);
        return dto(repository.save(entity));
    }

    @Transactional
    public ProfissionalDtos.Response atualizar(String emailAutenticado, ProfissionalDtos.Request request) {
        var entity = repository.findByUsuariaEmailIgnoreCase(emailAutenticado)
            .orElseThrow(() -> new ResourceNotFoundException("Perfil profissional não encontrado."));
        var tipo = tipos.findById(request.tipoServicoId()).orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
        entity.atualizar(tipo, request.registroProfissional(), request.biografia(), request.cidadeUf(), resolverUnidades(request.unidadeIds()));
        return dto(repository.save(entity));
    }

    @Transactional
    public void desativar(String emailAutenticado) {
        var entity = repository.findByUsuariaEmailIgnoreCase(emailAutenticado)
            .orElseThrow(() -> new ResourceNotFoundException("Perfil profissional não encontrado."));
        entity.desativar();
        repository.save(entity);
    }

    @Transactional
    public ProfissionalDtos.Response verificar(UUID id) {
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrada."));
        entity.verificar();
        return dto(repository.save(entity));
    }

    private Set<UnidadeApoio> resolverUnidades(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        var result = unidades.findAllById(ids);
        if (result.size() != new HashSet<>(ids).size()) {
            throw new IllegalArgumentException("Uma ou mais unidades de apoio não existem.");
        }
        return new HashSet<>(result);
    }

    private ProfissionalDtos.Response dto(ProfissionalVoluntaria entity) {
        return new ProfissionalDtos.Response(entity.getId(), entity.getTipoServico().getId(),
            entity.getTipoServico().getNome(), entity.getRegistroProfissional(), entity.getBiografia(),
            entity.getCidadeUf(), entity.isAtiva(), entity.getVerificadoEm(), entity.getCriadoEm(),
            entity.getUnidades().stream().map(u -> new ProfissionalDtos.Unidade(u.getId(), u.getNome(), u.getCidade(), u.getBairro())).toList());
    }
}
