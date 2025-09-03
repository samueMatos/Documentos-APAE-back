package br.apae.ged.domain.repositories.specifications;

import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Document;
import br.apae.ged.domain.models.Pessoa;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {

    public static Specification<Document> isLast() {
        return (root, query, cb) -> cb.isTrue(root.get("isLast"));
    }

    public static Specification<Document> isAtivo() {
        return (root, query, cb) -> cb.isTrue(root.get("isAtivo"));
    }

    /**
     * Cria uma especificação para buscar documentos com base em um termo de busca.
     * A busca é polimórfica e funciona para qualquer subclasse de Pessoa.
     * - Busca por Nome (para todos os tipos de Pessoa)
     * - Busca por CPF (para todos os tipos de Pessoa)
     * - Busca por Matrícula (apenas para Alunos)
     */
    public static Specification<Document> byTermoBusca(String termoBusca) {
        if (termoBusca == null || termoBusca.isBlank()) {
            return null;
        }

        return (root, query, cb) -> {
            // Join com a entidade base Pessoa
            Join<Document, Pessoa> pessoaJoin = root.join("pessoa");

            // Left Join com a entidade Alunos para acessar o campo 'matricula'
            // O Left Join garante que mesmo que a Pessoa não seja um Aluno, a consulta
            // funcione
            Join<Pessoa, Alunos> alunoJoin = cb.treat(pessoaJoin, Pessoa.class).join("id", JoinType.LEFT);

            // Predicado para buscar pelo nome em Pessoa
            Predicate nomePredicate = cb.like(cb.lower(pessoaJoin.get("nome")), "%" + termoBusca.toLowerCase() + "%");

            // Predicado para buscar pelo CPF em Pessoa
            String cpfNumeros = termoBusca.replaceAll("[^0-9]", "");
            Predicate cpfPredicate = null;
            if (!cpfNumeros.isEmpty()) {
                Expression<String> cpfSemPontuacao = cb.function("REPLACE", String.class,
                        cb.function("REPLACE", String.class, pessoaJoin.get("cpf").get("cpf"), cb.literal("."),
                                cb.literal("")),
                        cb.literal("-"), cb.literal(""));
                cpfPredicate = cb.like(cpfSemPontuacao, "%" + cpfNumeros + "%");
            }

            // Predicado para buscar pela matrícula em Alunos
            Predicate matriculaPredicate = null;
            if (termoBusca.matches(".*[0-9].*")) { // Apenas tenta buscar por matrícula se houver números
                matriculaPredicate = cb.like(alunoJoin.get("matricula"), "%" + termoBusca + "%");
            }

            // Combina os predicados com OR
            if (cpfPredicate != null && matriculaPredicate != null) {
                return cb.or(nomePredicate, cpfPredicate, matriculaPredicate);
            } else if (matriculaPredicate != null) {
                return cb.or(nomePredicate, matriculaPredicate);
            } else if (cpfPredicate != null) {
                return cb.or(nomePredicate, cpfPredicate);
            } else {
                return nomePredicate;
            }
        };
    }
}