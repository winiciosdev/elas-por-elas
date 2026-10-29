package br.com.redeapoio.endereco;
import jakarta.persistence.*; import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name="endereco") public class Endereco {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 private String cep; private String logradouro; private String numero; private String complemento; private String bairro; private String cidade; private String uf;
 private BigDecimal latitude; private BigDecimal longitude;
 protected Endereco() {}
 public UUID getId(){return id;} public String getCep(){return cep;} public void setCep(String v){cep=v;}
 public String getLogradouro(){return logradouro;} public void setLogradouro(String v){logradouro=v;}
 public String getNumero(){return numero;} public void setNumero(String v){numero=v;}
 public String getComplemento(){return complemento;} public void setComplemento(String v){complemento=v;}
 public String getBairro(){return bairro;} public void setBairro(String v){bairro=v;}
 public String getCidade(){return cidade;} public void setCidade(String v){cidade=v;}
 public String getUf(){return uf;} public void setUf(String v){uf=v;}
 public BigDecimal getLatitude(){return latitude;} public void setLatitude(BigDecimal v){latitude=v;}
 public BigDecimal getLongitude(){return longitude;} public void setLongitude(BigDecimal v){longitude=v;}
}
