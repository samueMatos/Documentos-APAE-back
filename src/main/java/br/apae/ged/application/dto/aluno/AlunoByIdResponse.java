package br.apae.ged.application.dto.aluno;

import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Endereco;

import java.time.LocalDate;

public record AlunoByIdResponse(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String cpf,
        String telefone,
        String deficiencia,
        String cpfResponsavel,
        String estado,
        String cidade,
        String bairro,
        String rua,
        String complemento,
        String cep

) {
    public static AlunoByIdResponse fromEntity(Alunos aluno, Endereco endereco) {
        return new AlunoByIdResponse(
                aluno.getNome(),
                aluno.getDataNascimento(),
                aluno.getSexo(),
                aluno.getCpf().getCpf(),
                aluno.getTelefone(),
                aluno.getDeficiencia(),
                aluno.getCpfResponsavel(),
                endereco.getCidade().getEstado().getNome(),
                endereco.getCidade().getNome(),
                endereco.getBairro(),
                endereco.getRua(),
                endereco.getComplemento(),
                endereco.getCep()
        );
    }

}
