package br.com.redeapoio.tiposervico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface TipoServicoRepository extends JpaRepository<TipoServico, UUID> {}
