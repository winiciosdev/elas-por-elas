package br.com.redeapoio.profissional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
public final class ProfissionalDtos {
    private ProfissionalDtos() {}
    public record Request(@NotNull UUID tipoServicoId, @Size(max=100) String registroProfissional,
        @Size(max=5000) String biografia, @Size(max=100) String cidadeUf) {}
    public record Response(UUID id, UUID tipoServicoId, String tipoServico,
        String registroProfissional, String biografia, String cidadeUf, boolean ativa, Instant verificadoEm, Instant criadoEm) {}
}
