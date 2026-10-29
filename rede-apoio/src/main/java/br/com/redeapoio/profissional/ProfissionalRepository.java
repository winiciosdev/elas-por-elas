package br.com.redeapoio.profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProfissionalRepository extends JpaRepository<ProfissionalVoluntaria, UUID> {
    Optional<ProfissionalVoluntaria> findByUsuariaEmailIgnoreCase(String email);
    List<ProfissionalVoluntaria> findByAtivaTrue();
    List<ProfissionalVoluntaria> findByAtivaTrueAndCidadeUfContainingIgnoreCase(String cidadeUf);
}
