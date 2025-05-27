package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    Optional<Cidade> findByIbge(String ibge);

}
