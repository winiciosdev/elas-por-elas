package br.com.redeapoio.auth;

import br.com.redeapoio.profissional.ProfissionalDtos;
import br.com.redeapoio.profissional.ProfissionalService;
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
    private final ProfissionalService profissionais;
    private final long expirationMinutes;

    public AuthService(UsuariaRepository repository, PasswordEncoder encoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       ProfissionalService profissionais,
                       @Value("${app.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.repository = repository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.profissionais = profissionais;
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

    @Transactional
    public AuthDtos.CadastroVoluntariaResponse cadastrarVoluntaria(AuthDtos.CadastroVoluntariaRequest request) {
        String email = request.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Já existe uma conta com este e-mail.");
        }
        Usuaria nova = new Usuaria(email, encoder.encode(request.senha()), request.cidadeUf());
        nova.setNome(request.nome().trim());
        nova.tornarVoluntaria();
        Usuaria usuaria = repository.save(nova);
        var perfil = profissionais.criarParaUsuaria(usuaria, new ProfissionalDtos.Request(null,
            request.registroProfissional().trim(), null, request.cidadeUf(), null, request.telefone(), request.area(),
            null, null, null, null, null, null));
        return new AuthDtos.CadastroVoluntariaResponse(response(usuaria), perfil);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.senha()));
        Usuaria usuaria = repository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));
        return response(usuaria);
    }

    @Transactional(readOnly = true)
    public AuthDtos.UsuariaResponse me(String emailAutenticado) {
        var usuaria = repository.findByEmailIgnoreCase(emailAutenticado)
            .orElseThrow(() -> new IllegalArgumentException("Usuária não encontrada."));
        return usuariaResponse(usuaria);
    }

    @Transactional
    public AuthDtos.UsuariaResponse atualizarMe(AuthDtos.AtualizarUsuariaRequest request, String emailAutenticado) {
        var usuaria = repository.findByEmailIgnoreCase(emailAutenticado)
            .orElseThrow(() -> new IllegalArgumentException("Usuária não encontrada."));
        usuaria.setCidadeUf(request.cidadeUf());
        repository.save(usuaria);
        return usuariaResponse(usuaria);
    }

    private AuthDtos.AuthResponse response(Usuaria usuaria) {
        var principal = org.springframework.security.core.userdetails.User
            .withUsername(usuaria.getEmail()).password(usuaria.getSenhaHash()).roles(usuaria.getRole()).build();
        return new AuthDtos.AuthResponse(jwtService.generateToken(principal), "Bearer", expirationMinutes,
            usuariaResponse(usuaria));
    }

    private AuthDtos.UsuariaResponse usuariaResponse(Usuaria usuaria) {
        return new AuthDtos.UsuariaResponse(usuaria.getId(), usuaria.getNome(), usuaria.getEmail(),
            usuaria.getCidadeUf(), usuaria.getRole(), usuaria.getCriadoEm());
    }
}
