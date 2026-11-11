package br.com.redeapoio.meuapoio;

import br.com.redeapoio.usuaria.Usuaria;
import br.com.redeapoio.unidadeapoio.UnidadeApoio;
import br.com.redeapoio.profissional.ProfissionalVoluntaria;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="meu_apoio")
public class MeuApoio {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(optional=false) @JoinColumn(name="usuaria_id",nullable=false) private Usuaria usuaria;
    @ManyToOne @JoinColumn(name="unidade_id") private UnidadeApoio unidade;
    @ManyToOne @JoinColumn(name="profissional_id") private ProfissionalVoluntaria profissional;
    @Column(name="criado_em",nullable=false,updatable=false) private Instant criadoEm=Instant.now();
    protected MeuApoio() {}
    public MeuApoio(Usuaria u,UnidadeApoio unidade,ProfissionalVoluntaria profissional){this.usuaria=u;this.unidade=unidade;this.profissional=profissional;}
    public UUID getId(){return id;} public Usuaria getUsuaria(){return usuaria;} public UnidadeApoio getUnidade(){return unidade;}
    public ProfissionalVoluntaria getProfissional(){return profissional;} public Instant getCriadoEm(){return criadoEm;}
}
