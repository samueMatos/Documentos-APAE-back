package br.apae.ged.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.apae.ged.domain.models.TipoDocumento;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long>, JpaSpecificationExecutor<TipoDocumento> {
    Optional<TipoDocumento> findByNome(String nome);
    Optional<TipoDocumento> findByNomeIgnoreCase(String nome);
    List<TipoDocumento> findAllByIsAtivoTrueOrderByNomeAsc();
}
