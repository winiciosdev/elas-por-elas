package br.com.redeapoio.unidadeapoio;

import br.com.redeapoio.tiposervico.TipoServico;
import jakarta.persistence.*;
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
    @Column(nullable=false) private boolean ativa = true;
    @Column(name="verificado_em") private Instant verificadoEm;
    @Column(name="criado_em", nullable=false, updatable=false) private Instant criadoEm = Instant.now();
    @ManyToMany
    @JoinTable(name="unidade_servico",
        joinColumns=@JoinColumn(name="unidade_id"),
        inverseJoinColumns=@JoinColumn(name="tipo_servico_id"))
    private Set<TipoServico> servicos = new HashSet<>();

    protected UnidadeApoio() {}
    public UnidadeApoio(String nome, String descricao, String cidade, String bairro, String endereco, String telefone, String site) {
        this.nome=nome; this.descricao=descricao; this.cidade=cidade; this.bairro=bairro;
        this.endereco=endereco; this.telefone=telefone; this.site=site;
    }
    public UUID getId(){return id;} public String getNome(){return nome;} public String getDescricao(){return descricao;}
    public String getCidade(){return cidade;} public String getBairro(){return bairro;} public String getEndereco(){return endereco;}
    public String getTelefone(){return telefone;} public String getSite(){return site;} public boolean isAtiva(){return ativa;}
    public Instant getVerificadoEm(){return verificadoEm;} public Instant getCriadoEm(){return criadoEm;}
    public Set<TipoServico> getServicos(){return servicos;}
    public void atualizar(String nome,String descricao,String cidade,String bairro,String endereco,String telefone,String site,Set<TipoServico> servicos) {
        this.nome=nome; this.descricao=descricao; this.cidade=cidade; this.bairro=bairro; this.endereco=endereco;
        this.telefone=telefone; this.site=site; this.servicos=servicos;
    }
    public void verificar(){this.verificadoEm=Instant.now();}
}
