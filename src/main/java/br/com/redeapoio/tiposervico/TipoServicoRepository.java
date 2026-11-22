package br.com.redeapoio.tiposervico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface TipoServicoRepository extends JpaRepository<TipoServico, UUID> {
    Optional<TipoServico> findByCodigo(String codigo);
    List<TipoServico> findByCodigoIn(Collection<String> codigos);
}
