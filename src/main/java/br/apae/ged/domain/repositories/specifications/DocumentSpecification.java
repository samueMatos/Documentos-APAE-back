package br.apae.ged.domain.repositories.specifications;

import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Document;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {

    public static Specification<Document> isLast() {
        return (root, query, cb) -> cb.isTrue(root.get("isLast"));
    }

    public static Specification<Document> byAlunoNome(String nome) {
        if (nome == null || nome.isBlank()) return null;
        return (root, query, cb) -> {
            Join<Document, Alunos> alunoJoin = root.join("aluno");
            return cb.like(cb.lower(alunoJoin.get("nome")), "%" + nome.toLowerCase() + "%");
        };
    }

    public static Specification<Document> byAlunoMatricula(String matricula) {

        if (matricula == null || matricula.isBlank() || !matricula.matches(".*[0-9].*")) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Document, Alunos> alunoJoin = root.join("aluno");
            return cb.like(alunoJoin.get("matricula").as(String.class), "%" + matricula + "%");
        };
    }

    public static Specification<Document> byAlunoCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        String cpfNumeros = cpf.replaceAll("[^0-9]", "");

        if (cpfNumeros.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            Join<Document, Alunos> alunoJoin = root.join("aluno");

            Expression<String> cpfSemPontuacao = cb.function("REPLACE", String.class,
                    cb.function("REPLACE", String.class, alunoJoin.get("cpf"), cb.literal("."), cb.literal("")),
                    cb.literal("-"), cb.literal("")
            );
            return cb.like(cpfSemPontuacao, "%" + cpfNumeros + "%");
        };
    }

    public static Specification<Document> isAtivo() {
        return (root, query, cb) -> cb.isTrue(root.get("isAtivo"));
    }
}