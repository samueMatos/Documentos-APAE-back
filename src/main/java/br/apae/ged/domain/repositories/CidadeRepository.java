package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    Optional<Cidade> findByIbge(String ibge);
    Optional<Cidade> findByNomeIgnoreCaseAndEstadoUfIgnoreCase(String nome, String uf);
}
