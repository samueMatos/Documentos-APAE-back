package br.apae.ged.domain.repositories;

import br.apae.ged.domain.models.DocumentoInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoInstitucionalRepository
        extends JpaRepository<DocumentoInstitucional, Long>, JpaSpecificationExecutor<DocumentoInstitucional> {
}