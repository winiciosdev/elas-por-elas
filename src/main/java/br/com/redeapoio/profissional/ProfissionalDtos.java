package br.com.redeapoio.profissional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
public final class ProfissionalDtos {
    private ProfissionalDtos() {}
    public record Request(UUID tipoServicoId, @Size(max=100) String registroProfissional,
        @Size(max=5000) String biografia, @Size(max=100) String cidadeUf, List<UUID> unidadeIds,
        @Size(max=30) String telefone, @Size(max=30) String area, @Size(max=100) String profissao,
        @Size(max=100) String bairro, @Size(max=255) String endereco, List<String> modalidades,
        @Size(max=255) String disponibilidade, List<String> servicoCodigos) {}
    public record DisponibilidadeRequest(@NotNull Boolean disponivel) {}
    public record Unidade(UUID id, String nome, String cidade, String bairro) {}
    public record Servico(UUID id, String codigo, String nome) {}
    public record Response(UUID id, UUID tipoServicoId, String tipoServico, String nome, String telefone,
        String area, String profissao, String registroProfissional, String biografia, String cidadeUf, String bairro,
        String endereco, List<String> modalidades, String disponibilidade, List<Servico> servicos, String status,
        boolean ativa, Instant verificadoEm, Instant criadoEm, List<Unidade> unidades) {}
}
