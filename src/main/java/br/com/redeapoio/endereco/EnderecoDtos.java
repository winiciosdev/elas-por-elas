package br.com.redeapoio.endereco;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public final class EnderecoDtos {
    private EnderecoDtos() {}

    public record Request(String cep, String logradouro, String numero, String complemento, String bairro, String cidade,
                          @Size(min = 2, max = 2) String uf, BigDecimal latitude, BigDecimal longitude) {}

    public record Response(UUID id, String cep, String logradouro, String numero, String complemento, String bairro,
                           String cidade, String uf, BigDecimal latitude, BigDecimal longitude) {}
}
