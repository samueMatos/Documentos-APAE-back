package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.valueObjects.CPF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository <Alunos, Long>, JpaSpecificationExecutor<Alunos> {

    boolean existsByCpf(String cpf);
    Optional<Alunos> findByCpf(CPF cpf);
}
