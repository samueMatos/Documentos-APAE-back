package br.apae.ged.domain.repositories.specifications;

import br.apae.ged.domain.models.Alunos;
import jakarta.persistence.criteria.Expression;
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
        if (matricula == null || matricula.isBlank() || !matricula.matches(".*[0-9].*")) {

            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("matricula").as(String.class), "%" + matricula + "%");
    }


    public static Specification<Alunos> byCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        String cpfNumeros = cpf.replaceAll("[^0-9]", "");

        if (cpfNumeros.isEmpty()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            Expression<String> cpfSemPontuacao = criteriaBuilder.function("REPLACE", String.class,
                    criteriaBuilder.function("REPLACE", String.class, root.get("cpf"), criteriaBuilder.literal("."), criteriaBuilder.literal("")),
                    criteriaBuilder.literal("-"), criteriaBuilder.literal("")
            );
            return criteriaBuilder.like(cpfSemPontuacao, "%" + cpfNumeros + "%");
        };
    }
}

