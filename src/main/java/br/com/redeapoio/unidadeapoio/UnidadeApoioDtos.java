package br.com.redeapoio.unidadeapoio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
public final class UnidadeApoioDtos {
    private UnidadeApoioDtos() {}
    public record Request(@NotBlank @Size(max=255) String nome, String descricao,
        @NotBlank @Size(max=100) String cidade, @Size(max=100) String bairro,
        @Size(max=255) String endereco, @Size(max=40) String telefone, @Size(max=500) String site,
        List<UUID> tipoServicoIds, @Size(max=30) String categoria, @Size(max=100) String horarioFuncionamento,
        List<String> modalidades,
        @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
        @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude) {}
    public record Servico(UUID id, String codigo, String nome) {}
    public record Response(UUID id,String nome,String descricao,String cidade,String bairro,String endereco,
        String telefone,String site,boolean ativa,Instant verificadoEm,Instant criadoEm,List<Servico> servicos,
        String categoria,String horarioFuncionamento,List<String> modalidades,BigDecimal latitude,BigDecimal longitude) {}
}
