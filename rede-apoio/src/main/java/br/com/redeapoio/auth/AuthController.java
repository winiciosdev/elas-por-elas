package br.com.redeapoio.auth;

import br.com.redeapoio.usuaria.UsuariaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Registro, login e dados da usuária autenticada.")
public class AuthController {
    private final AuthService service;
    private final UsuariaRepository usuariaRepository;

    public AuthController(AuthService service, UsuariaRepository usuariaRepository) {
        this.service = service;
        this.usuariaRepository = usuariaRepository;
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar nova usuária", description = "Cria uma conta nova no sistema com role padrão de usuária e retorna o token de acesso.")
    public AuthDtos.AuthResponse cadastrar(@Valid @RequestBody AuthDtos.CadastroRequest request) {
        return service.cadastrar(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Fazer login", description = "Valida e-mail e senha e retorna um token JWT para uso nas rotas autenticadas.")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar dados da usuária logada", description = "Retorna as informações do perfil da usuária autenticada com base no token JWT.")
    public AuthDtos.UsuariaResponse me(Authentication authentication) {
        var u = usuariaRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        return new AuthDtos.UsuariaResponse(u.getId(), u.getNome(), u.getEmail(), u.getCidadeUf(), u.getRole(), u.getCriadoEm());
    }

    @PatchMapping("/me")
    @Operation(summary = "Atualizar dados da usuária logada", description = "Atualiza a cidade/UF da usuária autenticada e retorna o perfil atualizado.")
    public AuthDtos.UsuariaResponse atualizar(@Valid @RequestBody AuthDtos.AtualizarUsuariaRequest request, Authentication authentication) {
        var u = usuariaRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        u.setCidadeUf(request.cidadeUf());
        usuariaRepository.save(u);
        return new AuthDtos.UsuariaResponse(u.getId(), u.getNome(), u.getEmail(), u.getCidadeUf(), u.getRole(), u.getCriadoEm());
    }
}
