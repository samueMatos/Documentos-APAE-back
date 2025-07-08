package br.apae.ged.domain.repositories.specifications;

import br.apae.ged.domain.models.Alunos;
import org.springframework.data.jpa.domain.Specification;

public class AlunoSpecification {


    public static Specification<Alunos> isAtivo() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isAtivo"));
    }


    public static Specification<Alunos> byNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }


    public static Specification<Alunos> byMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->

                criteriaBuilder.like(root.get("matricula").as(String.class), "%" + matricula + "%");
    }

    public static Specification<Alunos> byCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("cpf").get("cpf"), "%" + cpf + "%");
    }
}

