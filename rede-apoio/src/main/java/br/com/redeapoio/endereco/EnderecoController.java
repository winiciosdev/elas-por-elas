package br.com.redeapoio.endereco;
import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import jakarta.validation.Valid; import jakarta.validation.constraints.Size; import java.math.BigDecimal; import java.util.*;
@RestController @RequestMapping("/api/v1/enderecos") public class EnderecoController {
 private final EnderecoRepository repo; public EnderecoController(EnderecoRepository repo){this.repo=repo;}
 public record Request(String cep,String logradouro,String numero,String complemento,String bairro,String cidade,@Size(min=2,max=2) String uf,BigDecimal latitude,BigDecimal longitude){}
 @GetMapping @PreAuthorize("hasRole('ADMIN')") public List<Endereco> listar(){return repo.findAll();}
 @GetMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Endereco buscar(@PathVariable UUID id){return repo.findById(id).orElseThrow();}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public Endereco criar(@Valid @RequestBody Request r){return repo.save(map(r,new Endereco()));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Endereco atualizar(@PathVariable UUID id,@Valid @RequestBody Request r){return repo.save(map(r,repo.findById(id).orElseThrow()));}
 private Endereco map(Request r,Endereco e){e.setCep(r.cep());e.setLogradouro(r.logradouro());e.setNumero(r.numero());e.setComplemento(r.complemento());e.setBairro(r.bairro());e.setCidade(r.cidade());e.setUf(r.uf());e.setLatitude(r.latitude());e.setLongitude(r.longitude());return e;}
}
