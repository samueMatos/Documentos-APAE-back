package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Institucional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitucionalRepository extends JpaRepository<Institucional, Long> {

}
