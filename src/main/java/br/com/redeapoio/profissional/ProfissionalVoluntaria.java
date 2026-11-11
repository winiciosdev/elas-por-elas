package br.com.redeapoio.profissional;

import br.com.redeapoio.usuaria.Usuaria;
import br.com.redeapoio.tiposervico.TipoServico;
import br.com.redeapoio.unidadeapoio.UnidadeApoio;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="profissional_voluntaria")
public class ProfissionalVoluntaria {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @OneToOne(optional=false) @JoinColumn(name="usuaria_id",nullable=false,unique=true) private Usuaria usuaria;
    @ManyToOne(optional=false) @JoinColumn(name="tipo_servico_id",nullable=false) private TipoServico tipoServico;
    @Column(name="registro_profissional",length=100) private String registroProfissional;
    @Column(columnDefinition="text") private String biografia;
    @Column(name="cidade_uf",length=100) private String cidadeUf;
    @ManyToMany
    @JoinTable(name="profissional_unidade",
        joinColumns=@JoinColumn(name="profissional_id"),
        inverseJoinColumns=@JoinColumn(name="unidade_id"))
    private Set<UnidadeApoio> unidades = new HashSet<>();
    @Column(nullable=false) private boolean ativa=true;
    @Column(name="verificado_em") private Instant verificadoEm;
    @Column(name="criado_em",nullable=false,updatable=false) private Instant criadoEm=Instant.now();
    protected ProfissionalVoluntaria() {}
    public ProfissionalVoluntaria(Usuaria usuaria,TipoServico tipoServico,String registro,String biografia,String cidadeUf, Set<UnidadeApoio> unidades) {
        this.usuaria=usuaria;this.tipoServico=tipoServico;this.registroProfissional=registro;this.biografia=biografia;this.cidadeUf=cidadeUf;
        this.unidades=unidades;
    }
    public UUID getId(){return id;} public Usuaria getUsuaria(){return usuaria;} public TipoServico getTipoServico(){return tipoServico;}
    public String getRegistroProfissional(){return registroProfissional;} public String getBiografia(){return biografia;}
    public String getCidadeUf(){return cidadeUf;} public boolean isAtiva(){return ativa;} public Instant getVerificadoEm(){return verificadoEm;}
    public Instant getCriadoEm(){return criadoEm;} public Set<UnidadeApoio> getUnidades(){return unidades;}
    public void atualizar(TipoServico tipo,String registro,String bio,String cidade,Set<UnidadeApoio> unidades){this.tipoServico=tipo;this.registroProfissional=registro;this.biografia=bio;this.cidadeUf=cidade;this.unidades=unidades;}
    public void desativar(){this.ativa=false;}
    public void verificar(){this.verificadoEm=Instant.now();}
}
