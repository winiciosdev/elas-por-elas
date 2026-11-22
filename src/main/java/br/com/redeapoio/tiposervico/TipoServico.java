package br.com.redeapoio.tiposervico;

import jakarta.persistence.*;
import java.text.Normalizer;
import java.util.UUID;

@Entity
@Table(name = "tipo_servico")
public class TipoServico {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 100) private String nome;
    @Column(nullable = false, unique = true, length = 50) private String codigo;
    @Column(columnDefinition = "text") private String descricao;
    protected TipoServico() {}
    public TipoServico(String nome, String codigo, String descricao) {
        this.nome = nome.trim();
        this.codigo = normalizarCodigo(codigo, nome);
        this.descricao = descricao;
    }
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public void atualizar(String nome, String codigo, String descricao) {
        this.nome = nome.trim();
        this.codigo = normalizarCodigo(codigo, nome);
        this.descricao = descricao;
    }

    private static String normalizarCodigo(String codigo, String nome) {
        String base = (codigo == null || codigo.isBlank()) ? nome : codigo;
        String semAcento = Normalizer.normalize(base.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.replaceAll("[^A-Za-z0-9]+", "_").toUpperCase();
    }
}
