package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepository extends JpaRepository<Estado, Long> {
}
