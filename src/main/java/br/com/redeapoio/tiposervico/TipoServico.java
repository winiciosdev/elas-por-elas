package br.com.redeapoio.tiposervico;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tipo_servico")
public class TipoServico {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 100) private String nome;
    @Column(columnDefinition = "text") private String descricao;
    protected TipoServico() {}
    public TipoServico(String nome, String descricao) { this.nome = nome.trim(); this.descricao = descricao; }
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public void atualizar(String nome, String descricao) { this.nome = nome.trim(); this.descricao = descricao; }
}
