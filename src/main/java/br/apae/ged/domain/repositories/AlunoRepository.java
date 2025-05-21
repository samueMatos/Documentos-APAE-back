package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Alunos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository <Alunos, Long>, JpaSpecificationExecutor<Alunos> {

    boolean existsByCpf(String cpf);
}
