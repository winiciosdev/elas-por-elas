package br.com.redeapoio.tiposervico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
public final class TipoServicoDtos {
    private TipoServicoDtos() {}
    public record Request(@NotBlank @Size(max=100) String nome, String descricao) {}
    public record Response(UUID id, String nome, String descricao) {}
}
