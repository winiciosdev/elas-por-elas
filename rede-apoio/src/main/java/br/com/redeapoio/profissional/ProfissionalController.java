package br.com.redeapoio.profissional;

import br.com.redeapoio.common.ResourceNotFoundException;
import br.com.redeapoio.tiposervico.TipoServicoRepository;
import br.com.redeapoio.usuaria.UsuariaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/profissionais")
@Tag(name = "Profissionais", description = "Consulta e gestão de perfis de profissionais voluntárias.")
public class ProfissionalController {
    private final ProfissionalRepository repository;
    private final UsuariaRepository usuarias;
    private final TipoServicoRepository tipos;
    public ProfissionalController(ProfissionalRepository repository,UsuariaRepository usuarias,TipoServicoRepository tipos) {
        this.repository=repository;this.usuarias=usuarias;this.tipos=tipos;
    }
    @GetMapping @Transactional(readOnly=true)
    @Operation(summary = "Listar profissionais ativos", description = "Lista os perfis ativos de profissionais, com filtro opcional por cidade/UF.")
    public List<ProfissionalDtos.Response> listar(@RequestParam(required=false) String cidadeUf) {
        var list=cidadeUf==null?repository.findByAtivaTrue():repository.findByAtivaTrueAndCidadeUfContainingIgnoreCase(cidadeUf);
        return list.stream().map(this::dto).toList();
    }
    @GetMapping("/{id}") @Transactional(readOnly=true)
    @Operation(summary = "Buscar profissional por ID", description = "Retorna um perfil de profissional ativo pelo identificador.")
    public ProfissionalDtos.Response buscar(@PathVariable UUID id) {
        return dto(repository.findById(id).filter(ProfissionalVoluntaria::isAtiva)
            .orElseThrow(()->new ResourceNotFoundException("Profissional não encontrada.")));
    }
    @PostMapping("/me") @Transactional
    @Operation(summary = "Criar perfil profissional da usuária logada", description = "Cria o perfil profissional da usuária autenticada para um tipo de serviço específico.")
    public ProfissionalDtos.Response criar(@Valid @RequestBody ProfissionalDtos.Request r,Authentication auth) {
        if(repository.findByUsuariaEmailIgnoreCase(auth.getName()).isPresent())
            throw new IllegalArgumentException("Esta usuária já possui perfil profissional.");
        var u=usuarias.findByEmailIgnoreCase(auth.getName()).orElseThrow();
        var tipo=tipos.findById(r.tipoServicoId()).orElseThrow(()->new ResourceNotFoundException("Tipo de serviço não encontrado."));
        return dto(repository.save(new ProfissionalVoluntaria(u,tipo,r.registroProfissional(),r.biografia(),r.cidadeUf())));
    }
    @PutMapping("/me") @Transactional
    @Operation(summary = "Atualizar perfil profissional da usuária logada", description = "Atualiza os dados do perfil profissional da usuária autenticada.")
    public ProfissionalDtos.Response atualizar(@Valid @RequestBody ProfissionalDtos.Request r,Authentication auth) {
        var e=repository.findByUsuariaEmailIgnoreCase(auth.getName()).orElseThrow(()->new ResourceNotFoundException("Perfil profissional não encontrado."));
        var tipo=tipos.findById(r.tipoServicoId()).orElseThrow(()->new ResourceNotFoundException("Tipo de serviço não encontrado."));
        e.atualizar(tipo,r.registroProfissional(),r.biografia(),r.cidadeUf());return dto(repository.save(e));
    }
    @DeleteMapping("/me") @Transactional
    @Operation(summary = "Desativar perfil profissional da usuária logada", description = "Desativa o perfil profissional da usuária autenticada sem excluir os dados históricos.")
    public void desativar(Authentication auth) {
        var e=repository.findByUsuariaEmailIgnoreCase(auth.getName()).orElseThrow(()->new ResourceNotFoundException("Perfil profissional não encontrado."));
        e.desativar();repository.save(e);
    }
    @PostMapping("/{id}/verificacao") @PreAuthorize("hasRole('ADMIN')") @Transactional
    @Operation(summary = "Verificar profissional", description = "Marca um perfil profissional como verificado pela administração.")
    public ProfissionalDtos.Response verificar(@PathVariable UUID id) {
        var e=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Profissional não encontrada."));
        e.verificar();return dto(repository.save(e));
    }
    private ProfissionalDtos.Response dto(ProfissionalVoluntaria e) {
        return new ProfissionalDtos.Response(e.getId(),e.getTipoServico().getId(),
            e.getTipoServico().getNome(),e.getRegistroProfissional(),e.getBiografia(),e.getCidadeUf(),e.isAtiva(),e.getVerificadoEm(),e.getCriadoEm());
    }
}
