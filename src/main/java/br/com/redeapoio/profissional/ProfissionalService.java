package br.com.redeapoio.profissional;

import br.com.redeapoio.common.Csv;
import br.com.redeapoio.common.ResourceNotFoundException;
import br.com.redeapoio.tiposervico.TipoServico;
import br.com.redeapoio.tiposervico.TipoServicoRepository;
import br.com.redeapoio.unidadeapoio.UnidadeApoio;
import br.com.redeapoio.unidadeapoio.UnidadeApoioRepository;
import br.com.redeapoio.usuaria.Usuaria;
import br.com.redeapoio.usuaria.UsuariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        var list = cidadeUf == null ? repository.findByAtivaTrueAndVerificadoEmIsNotNull()
            : repository.findByAtivaTrueAndVerificadoEmIsNotNullAndCidadeUfContainingIgnoreCase(cidadeUf);
        return list.stream().map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public ProfissionalDtos.Response buscar(UUID id) {
        var entity = repository.findById(id).filter(p -> p.isAtiva() && p.getVerificadoEm() != null)
            .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrada."));
        return dto(entity);
    }

    @Transactional(readOnly = true)
    public ProfissionalDtos.Response meu(String emailAutenticado) {
        return dto(repository.findByUsuariaEmailIgnoreCase(emailAutenticado)
            .orElseThrow(() -> new ResourceNotFoundException("Perfil profissional não encontrado.")));
    }

    @Transactional
    public ProfissionalDtos.Response criar(String emailAutenticado, ProfissionalDtos.Request request) {
        var usuaria = usuarias.findByEmailIgnoreCase(emailAutenticado).orElseThrow(() -> new ResourceNotFoundException("Usuária não encontrada."));
        return criarParaUsuaria(usuaria, request);
    }

    @Transactional
    public ProfissionalDtos.Response criarParaUsuaria(Usuaria usuaria, ProfissionalDtos.Request request) {
        if (repository.findByUsuariaEmailIgnoreCase(usuaria.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Esta usuária já possui perfil profissional.");
        }
        var servicos = resolverServicos(request);
        var entity = new ProfissionalVoluntaria(usuaria, request, servicos.principal(), servicos.todos(), resolverUnidades(request.unidadeIds()));
        return dto(repository.save(entity));
    }

    @Transactional
    public ProfissionalDtos.Response atualizar(String emailAutenticado, ProfissionalDtos.Request request) {
        var entity = repository.findByUsuariaEmailIgnoreCase(emailAutenticado)
            .orElseThrow(() -> new ResourceNotFoundException("Perfil profissional não encontrado."));
        var servicos = resolverServicos(request);
        entity.atualizar(request, servicos.principal(), servicos.todos(), resolverUnidades(request.unidadeIds()));
        return dto(repository.save(entity));
    }

    @Transactional
    public ProfissionalDtos.Response atualizarDisponibilidade(String emailAutenticado, boolean disponivel) {
        var entity = repository.findByUsuariaEmailIgnoreCase(emailAutenticado)
            .orElseThrow(() -> new ResourceNotFoundException("Perfil profissional não encontrado."));
        entity.atualizarDisponibilidade(disponivel);
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

    private record Servicos(TipoServico principal, Set<TipoServico> todos) {}

    private static final Map<String, String> CODIGO_POR_AREA = Map.of(
        "PSICOLOGIA", "PSICOLOGICO", "DIREITO", "JURIDICO", "SERVICO_SOCIAL", "ASSISTENCIA_SOCIAL",
        "SAUDE", "SAUDE_MULHER", "CARREIRA", "ORIENTACAO_PROFISSIONAL", "OUTRA", "OUTROS");

    private Servicos resolverServicos(ProfissionalDtos.Request request) {
        var codigos = request.servicoCodigos() == null ? List.<String>of() : request.servicoCodigos();
        Set<TipoServico> todos = codigos.isEmpty() ? new HashSet<>() : new HashSet<>(tipos.findByCodigoIn(codigos));
        if (todos.size() != new HashSet<>(codigos).size()) {
            throw new IllegalArgumentException("Um ou mais tipos de serviço não existem.");
        }
        TipoServico principal;
        if (request.tipoServicoId() != null) {
            principal = tipos.findById(request.tipoServicoId()).orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
        } else if (!codigos.isEmpty()) {
            principal = todos.stream().filter(t -> t.getCodigo().equals(codigos.get(0))).findFirst().orElseThrow();
        } else if (request.area() != null && CODIGO_POR_AREA.containsKey(request.area())) {
            principal = tipos.findByCodigo(CODIGO_POR_AREA.get(request.area()))
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
        } else {
            throw new IllegalArgumentException("Informe o tipo de serviço, os serviços ou a área de atuação.");
        }
        todos.add(principal);
        return new Servicos(principal, todos);
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
            entity.getTipoServico().getNome(), entity.getUsuaria().getNome(), entity.getTelefoneContato(),
            entity.getArea(), entity.getProfissao(), entity.getRegistroProfissional(), entity.getBiografia(),
            entity.getCidadeUf(), entity.getBairro(), entity.getEnderecoAtendimento(), Csv.dividir(entity.getModalidades()),
            entity.getDisponibilidade(),
            entity.getServicos().stream().map(s -> new ProfissionalDtos.Servico(s.getId(), s.getCodigo(), s.getNome())).toList(),
            entity.getVerificadoEm() == null ? "EM_ANALISE" : "APROVADA",
            entity.isAtiva(), entity.getVerificadoEm(), entity.getCriadoEm(),
            entity.getUnidades().stream().map(u -> new ProfissionalDtos.Unidade(u.getId(), u.getNome(), u.getCidade(), u.getBairro())).toList());
    }
}
