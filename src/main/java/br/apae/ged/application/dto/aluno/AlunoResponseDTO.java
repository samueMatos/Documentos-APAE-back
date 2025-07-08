package br.apae.ged.application.dto.aluno;

import br.apae.ged.domain.models.Alunos;

import java.time.LocalDate;

public record AlunoResponseDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String cpf,
        String matricula
) {
    public static AlunoResponseDTO fromEntity(Alunos alunos){
        return new AlunoResponseDTO(
                alunos.getId(),
                alunos.getNome(),
                alunos.getDataNascimento(),
                alunos.getCpf().getCpf(),
                alunos.getMatricula()
        );
    }
}
