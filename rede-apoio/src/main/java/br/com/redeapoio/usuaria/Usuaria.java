package br.com.redeapoio.usuaria;

import jakarta.persistence.*;
import br.com.redeapoio.endereco.Endereco;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuaria")
public class Usuaria {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 255)
    private String nome = "";
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;
    @Column(name = "cidade_uf", length = 100)
    private String cidadeUf;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "endereco_id")
    private Endereco endereco;
    @Column(nullable = false, length = 20)
    private String role = "USER";
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm = Instant.now();

    protected Usuaria() {}
    public Usuaria(String email, String senhaHash, String cidadeUf) {
        this.email = email.toLowerCase().trim();
        this.senhaHash = senhaHash;
        this.cidadeUf = cidadeUf;
    }
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public String getCidadeUf() { return cidadeUf; }
    public String getRole() { return role; }
    public Instant getCriadoEm() { return criadoEm; }
    public void setCidadeUf(String cidadeUf) { this.cidadeUf = cidadeUf; }
}
