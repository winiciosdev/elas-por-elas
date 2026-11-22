package br.com.redeapoio.unidadeapoio;

import br.com.redeapoio.common.Csv;
import br.com.redeapoio.profissional.ProfissionalVoluntaria;
import br.com.redeapoio.tiposervico.TipoServico;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "unidade_apoio")
public class UnidadeApoio {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable=false) private String nome;
    @Column(columnDefinition="text") private String descricao;
    @Column(nullable=false, length=100) private String cidade;
    @Column(length=100) private String bairro;
    @Column(length=255) private String endereco;
    @Column(length=40) private String telefone;
    @Column(length=500) private String site;
    @Column(length=30) private String categoria;
    @Column(name="horario_funcionamento", length=100) private String horarioFuncionamento;
    @Column(length=100) private String modalidades;
    @Column(precision=10, scale=8) private BigDecimal latitude;
    @Column(precision=11, scale=8) private BigDecimal longitude;
    @Column(nullable=false) private boolean ativa = true;
    @Column(name="verificado_em") private Instant verificadoEm;
    @Column(name="criado_em", nullable=false, updatable=false) private Instant criadoEm = Instant.now();
    @ManyToMany
    @JoinTable(name="unidade_servico",
        joinColumns=@JoinColumn(name="unidade_id"),
        inverseJoinColumns=@JoinColumn(name="tipo_servico_id"))
    private Set<TipoServico> servicos = new HashSet<>();
    @ManyToMany(mappedBy = "unidades")
    private Set<ProfissionalVoluntaria> profissionais = new HashSet<>();

    protected UnidadeApoio() {}
    public UnidadeApoio(UnidadeApoioDtos.Request r, Set<TipoServico> servicos) {
        atualizar(r, servicos);
    }
    public UUID getId(){return id;} public String getNome(){return nome;} public String getDescricao(){return descricao;}
    public String getCidade(){return cidade;} public String getBairro(){return bairro;} public String getEndereco(){return endereco;}
    public String getTelefone(){return telefone;} public String getSite(){return site;} public boolean isAtiva(){return ativa;}
    public Instant getVerificadoEm(){return verificadoEm;} public Instant getCriadoEm(){return criadoEm;}
    public Set<TipoServico> getServicos(){return servicos;} public Set<ProfissionalVoluntaria> getProfissionais(){return profissionais;}
    public String getCategoria(){return categoria;} public String getHorarioFuncionamento(){return horarioFuncionamento;}
    public String getModalidades(){return modalidades;} public BigDecimal getLatitude(){return latitude;} public BigDecimal getLongitude(){return longitude;}
    public void atualizar(UnidadeApoioDtos.Request r, Set<TipoServico> servicos) {
        this.nome=r.nome(); this.descricao=r.descricao(); this.cidade=r.cidade(); this.bairro=r.bairro(); this.endereco=r.endereco();
        this.telefone=r.telefone(); this.site=r.site(); this.servicos=servicos;
        this.categoria=r.categoria(); this.horarioFuncionamento=r.horarioFuncionamento();
        this.modalidades=Csv.juntar(r.modalidades());
        this.latitude=r.latitude(); this.longitude=r.longitude();
    }
    public void verificar(){this.verificadoEm=Instant.now();}
}
