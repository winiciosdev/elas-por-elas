package br.com.redeapoio.meuapoio;

import br.com.redeapoio.common.ResourceNotFoundException;
import br.com.redeapoio.profissional.ProfissionalRepository;
import br.com.redeapoio.unidadeapoio.UnidadeApoioRepository;
import br.com.redeapoio.usuaria.UsuariaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/meu-apoio")
@Tag(name = "Meu Apoio", description = "Lista e gerencia as unidades e profissionais salvos pela usuária.")
public class MeuApoioController {
    private final MeuApoioRepository repository;
    private final UsuariaRepository usuarias;
    private final UnidadeApoioRepository unidades;
    private final ProfissionalRepository profissionais;
    public MeuApoioController(MeuApoioRepository repository,UsuariaRepository usuarias,
        UnidadeApoioRepository unidades,ProfissionalRepository profissionais) {
        this.repository=repository;this.usuarias=usuarias;this.unidades=unidades;this.profissionais=profissionais;
    }
    @GetMapping @Transactional(readOnly=true)
    @Operation(summary = "Listar itens salvos", description = "Retorna todas as unidades e profissionais marcados como favoritos pela usuária autenticada.")
    public List<MeuApoioDtos.Response> listar(Authentication auth) {
        return repository.findByUsuariaEmailIgnoreCaseOrderByCriadoEmDesc(auth.getName()).stream().map(this::dto).toList();
    }
    @PostMapping @Transactional
    @Operation(summary = "Salvar unidade ou profissional", description = "Adiciona uma unidade ou profissional à lista de favoritos da usuária autenticada.")
    public MeuApoioDtos.Response salvar(@Valid @RequestBody MeuApoioDtos.Request r,Authentication auth) {
        var u=usuarias.findByEmailIgnoreCase(auth.getName()).orElseThrow();
        if(r.unidadeId()!=null) {
            if(repository.existsByUsuariaEmailIgnoreCaseAndUnidadeId(auth.getName(),r.unidadeId()))
                throw new IllegalArgumentException("Esta unidade já está salva nos seus apoios.");
            var unidade=unidades.findById(r.unidadeId()).orElseThrow(()->new ResourceNotFoundException("Unidade não encontrada."));
            return dto(repository.save(new MeuApoio(u,unidade,null)));
        }
        if(repository.existsByUsuariaEmailIgnoreCaseAndProfissionalId(auth.getName(),r.profissionalId()))
            throw new IllegalArgumentException("Esta profissional já está salva nos seus apoios.");
        var profissional=profissionais.findById(r.profissionalId()).orElseThrow(()->new ResourceNotFoundException("Profissional não encontrada."));
        return dto(repository.save(new MeuApoio(u,null,profissional)));
    }
    @DeleteMapping("/{id}") @Transactional
    @Operation(summary = "Remover item salvo", description = "Remove uma unidade ou profissional da lista de favoritos da usuária autenticada.")
    public void remover(@PathVariable UUID id,Authentication auth) {
        var item=repository.findByIdAndUsuariaEmailIgnoreCase(id,auth.getName())
            .orElseThrow(()->new ResourceNotFoundException("Favorito não encontrado."));
        repository.delete(item);
    }
    private MeuApoioDtos.Response dto(MeuApoio e) {
        return new MeuApoioDtos.Response(e.getId(),
            e.getUnidade()==null?null:e.getUnidade().getId(),
            e.getUnidade()==null?null:e.getUnidade().getNome(),
            e.getProfissional()==null?null:e.getProfissional().getId(),
            e.getProfissional()==null?null:e.getProfissional().getUsuaria().getEmail(),
            e.getCriadoEm());
    }
}
