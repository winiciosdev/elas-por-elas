package br.com.redeapoio.auth;

import br.com.redeapoio.usuaria.Usuaria;
import br.com.redeapoio.usuaria.UsuariaRepository;
import br.com.redeapoio.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UsuariaRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final long expirationMinutes;

    public AuthService(UsuariaRepository repository, PasswordEncoder encoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       @Value("${app.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.repository = repository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.expirationMinutes = expirationMinutes;
    }

    @Transactional
    public AuthDtos.AuthResponse cadastrar(AuthDtos.CadastroRequest request) {
        String email = request.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Já existe uma conta com este e-mail.");
        }
        Usuaria nova = new Usuaria(email, encoder.encode(request.senha()), request.cidadeUf());
        nova.setNome(request.nome().trim());
        Usuaria usuaria = repository.save(nova);
        return response(usuaria);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.senha()));
        Usuaria usuaria = repository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));
        return response(usuaria);
    }

    private AuthDtos.AuthResponse response(Usuaria usuaria) {
        var principal = org.springframework.security.core.userdetails.User
            .withUsername(usuaria.getEmail()).password(usuaria.getSenhaHash()).roles(usuaria.getRole()).build();
        return new AuthDtos.AuthResponse(jwtService.generateToken(principal), "Bearer", expirationMinutes,
            new AuthDtos.UsuariaResponse(usuaria.getId(), usuaria.getNome(), usuaria.getEmail(), usuaria.getCidadeUf(), usuaria.getRole(), usuaria.getCriadoEm()));
    }
}
