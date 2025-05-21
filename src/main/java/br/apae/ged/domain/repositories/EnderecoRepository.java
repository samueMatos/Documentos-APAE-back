package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    Endereco findByAluno(Alunos aluno);
}
