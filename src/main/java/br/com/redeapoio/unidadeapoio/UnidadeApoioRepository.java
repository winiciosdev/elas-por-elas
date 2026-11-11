package br.com.redeapoio.unidadeapoio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface UnidadeApoioRepository extends JpaRepository<UnidadeApoio, UUID> {
    List<UnidadeApoio> findByAtivaTrue();
    List<UnidadeApoio> findByAtivaTrueAndCidadeContainingIgnoreCase(String cidade);
    List<UnidadeApoio> findByAtivaTrueAndCidadeContainingIgnoreCaseAndBairroContainingIgnoreCase(String cidade, String bairro);
}
