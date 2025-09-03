package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Colaborador;
import br.apae.ged.domain.valueObjects.CPF;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, Long>, JpaSpecificationExecutor<Colaborador> {
    Optional<Colaborador> findByCpf(CPF cpf);
}