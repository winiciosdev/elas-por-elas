package br.com.redeapoio.auth;

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

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar nova usuária", description = "Cria uma conta nova no sistema com role padrão de usuária e retorna o token de acesso.")
    public AuthDtos.AuthResponse cadastrar(@Valid @RequestBody AuthDtos.CadastroRequest request) {
        return service.cadastrar(request);
    }

    @PostMapping("/cadastro-voluntaria")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar voluntária", description = "Cria a conta e o perfil profissional (em análise) em uma única operação e retorna o token de acesso.")
    public AuthDtos.CadastroVoluntariaResponse cadastrarVoluntaria(@Valid @RequestBody AuthDtos.CadastroVoluntariaRequest request) {
        return service.cadastrarVoluntaria(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Fazer login", description = "Valida e-mail e senha e retorna um token JWT para uso nas rotas autenticadas.")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar dados da usuária logada", description = "Retorna as informações do perfil da usuária autenticada com base no token JWT.")
    public AuthDtos.UsuariaResponse me(Authentication authentication) {
        return service.me(authentication.getName());
    }

    @PatchMapping("/me")
    @Operation(summary = "Atualizar dados da usuária logada", description = "Atualiza a cidade/UF da usuária autenticada e retorna o perfil atualizado.")
    public AuthDtos.UsuariaResponse atualizar(@Valid @RequestBody AuthDtos.AtualizarUsuariaRequest request, Authentication authentication) {
        return service.atualizarMe(request, authentication.getName());
    }
}
