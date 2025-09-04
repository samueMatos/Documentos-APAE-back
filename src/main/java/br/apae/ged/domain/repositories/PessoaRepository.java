package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Pessoa;
import br.apae.ged.domain.valueObjects.CPF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findByCpf(CPF cpf);

    boolean existsByCpf(CPF cpf);
}