package br.com.redeapoio.meuapoio;
import jakarta.validation.constraints.AssertTrue;
import java.time.Instant;
import java.util.UUID;
public final class MeuApoioDtos {
    private MeuApoioDtos() {}
    public record Request(UUID unidadeId, UUID profissionalId) {
        @AssertTrue(message="Informe exatamente unidadeId ou profissionalId.")
        public boolean isAlvoValido(){return (unidadeId!=null) ^ (profissionalId!=null);}
    }
    public record Response(UUID id, UUID unidadeId, String unidadeNome, UUID profissionalId,
        String profissionalNome, Instant criadoEm) {}
}
