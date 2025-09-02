package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.Institucional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InstitucionalRepository extends JpaRepository<Institucional, Long> {

    List<Institucional> findByTipoDocumento_NomeIgnoreCaseContainingAndTituloIgnoreCaseContainingAndDataDocumento(
            String tipoDocumento, String titulo, LocalDate dataDocumento);

    Page<Institucional> findAll(Specification<Institucional> spec, Pageable pageableComOrdenacao);
}
