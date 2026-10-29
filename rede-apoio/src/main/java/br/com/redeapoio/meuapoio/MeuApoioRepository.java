package br.com.redeapoio.meuapoio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface MeuApoioRepository extends JpaRepository<MeuApoio, UUID> {
    List<MeuApoio> findByUsuariaEmailIgnoreCaseOrderByCriadoEmDesc(String email);
    Optional<MeuApoio> findByIdAndUsuariaEmailIgnoreCase(UUID id,String email);
    boolean existsByUsuariaEmailIgnoreCaseAndUnidadeId(String email,UUID unidadeId);
    boolean existsByUsuariaEmailIgnoreCaseAndProfissionalId(String email,UUID profissionalId);
}
