package br.com.redeapoio.usuaria;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UsuariaRepository extends JpaRepository<Usuaria, UUID> {
    Optional<Usuaria> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
