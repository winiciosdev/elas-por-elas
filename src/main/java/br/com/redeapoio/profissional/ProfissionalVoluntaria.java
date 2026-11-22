package br.com.redeapoio.profissional;

import br.com.redeapoio.common.Csv;
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
    @Column(name="telefone_contacto",length=30) private String telefoneContato;
    @Column(length=30) private String area;
    @Column(length=100) private String profissao;
    @Column(length=100) private String bairro;
    @Column(name="endereco_atendimento",length=255) private String enderecoAtendimento;
    @Column(length=100) private String modalidades;
    @Column(length=255) private String disponibilidade;
    @ManyToMany
    @JoinTable(name="profissional_servico",
        joinColumns=@JoinColumn(name="profissional_id"),
        inverseJoinColumns=@JoinColumn(name="tipo_servico_id"))
    private Set<TipoServico> servicos = new HashSet<>();
    @ManyToMany
    @JoinTable(name="profissional_unidade",
        joinColumns=@JoinColumn(name="profissional_id"),
        inverseJoinColumns=@JoinColumn(name="unidade_id"))
    private Set<UnidadeApoio> unidades = new HashSet<>();
    @Column(nullable=false) private boolean ativa=true;
    @Column(name="verificado_em") private Instant verificadoEm;
    @Column(name="criado_em",nullable=false,updatable=false) private Instant criadoEm=Instant.now();
    protected ProfissionalVoluntaria() {}
    public ProfissionalVoluntaria(Usuaria usuaria, ProfissionalDtos.Request r, TipoServico tipoServico,
                                  Set<TipoServico> servicos, Set<UnidadeApoio> unidades) {
        this.usuaria=usuaria;
        atualizar(r, tipoServico, servicos, unidades);
    }
    public UUID getId(){return id;} public Usuaria getUsuaria(){return usuaria;} public TipoServico getTipoServico(){return tipoServico;}
    public String getRegistroProfissional(){return registroProfissional;} public String getBiografia(){return biografia;}
    public String getCidadeUf(){return cidadeUf;} public boolean isAtiva(){return ativa;} public Instant getVerificadoEm(){return verificadoEm;}
    public Instant getCriadoEm(){return criadoEm;} public Set<UnidadeApoio> getUnidades(){return unidades;}
    public String getTelefoneContato(){return telefoneContato;} public String getArea(){return area;} public String getProfissao(){return profissao;}
    public String getBairro(){return bairro;} public String getEnderecoAtendimento(){return enderecoAtendimento;}
    public String getModalidades(){return modalidades;} public String getDisponibilidade(){return disponibilidade;}
    public Set<TipoServico> getServicos(){return servicos;}
    public void atualizar(ProfissionalDtos.Request r, TipoServico tipo, Set<TipoServico> servicos, Set<UnidadeApoio> unidades) {
        this.tipoServico=tipo; this.registroProfissional=r.registroProfissional(); this.biografia=r.biografia(); this.cidadeUf=r.cidadeUf();
        this.telefoneContato=r.telefone(); this.area=r.area(); this.profissao=r.profissao(); this.bairro=r.bairro();
        this.enderecoAtendimento=r.endereco(); this.modalidades=Csv.juntar(r.modalidades()); this.disponibilidade=r.disponibilidade();
        this.servicos=servicos; this.unidades=unidades;
    }
    public void atualizarDisponibilidade(boolean disponivel){this.ativa=disponivel;}
    public void desativar(){this.ativa=false;}
    public void verificar(){this.verificadoEm=Instant.now();}
}
