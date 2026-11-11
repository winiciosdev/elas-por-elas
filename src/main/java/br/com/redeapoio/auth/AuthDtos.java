package br.com.redeapoio.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class AuthDtos {
    private AuthDtos() {}
    public record CadastroRequest(
        @NotBlank @Size(max = 255) String nome,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 72) String senha,
        @Size(max = 100) String cidadeUf
    ) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) {}
    public record UsuariaResponse(UUID id, String nome, String email, String cidadeUf, String role, Instant criadoEm) {}
    public record AuthResponse(String token, String tipo, long expiraEmMinutos, UsuariaResponse usuaria) {}
    public record AtualizarUsuariaRequest(@Size(max = 100) String cidadeUf) {}
}
