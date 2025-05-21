package br.apae.ged.application.dto.aluno;

import br.apae.ged.domain.models.Alunos;

public record AlunoResponseDTO(
        String nome,
        String cpf,
        String deficiencia
) {

    public static AlunoResponseDTO fromEntity(Alunos alunos){
        return new AlunoResponseDTO(
                alunos.getNome(),
                alunos.getCpf(),
                alunos.getDeficiencia()
        );
    }
}
