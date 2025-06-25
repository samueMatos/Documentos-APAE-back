package br.apae.ged.application.dto.aluno;

import br.apae.ged.domain.models.Alunos;

import java.time.LocalDate;

public record AlunoResponseDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String cpf
) {
    public static AlunoResponseDTO daEntidade(Alunos alunos){
        return new AlunoResponseDTO(
                alunos.getId(),
                alunos.getNome(),
                alunos.getDataNascimento(),
                alunos.getCpf().getCpf()
        );
    }
}
