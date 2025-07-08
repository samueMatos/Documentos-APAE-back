package br.apae.ged.domain.repositories.specifications;

import br.apae.ged.domain.models.TipoDocumento;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TipoDocumentoSpecification {




    public static Specification<TipoDocumento> isAtivo() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isAtivo"));
    }


    public static Specification<TipoDocumento> byNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }
}