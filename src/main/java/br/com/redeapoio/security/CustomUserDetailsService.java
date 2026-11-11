package br.com.redeapoio.security;

import br.com.redeapoio.usuaria.UsuariaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuariaRepository repository;

    public CustomUserDetailsService(UsuariaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuaria = repository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuária não encontrada"));
        return User.withUsername(usuaria.getEmail())
            .password(usuaria.getSenhaHash())
            .roles(usuaria.getRole())
            .build();
    }
}
