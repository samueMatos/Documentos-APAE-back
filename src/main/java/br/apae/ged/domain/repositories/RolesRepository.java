package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Roles findByNome(String nome);
}
