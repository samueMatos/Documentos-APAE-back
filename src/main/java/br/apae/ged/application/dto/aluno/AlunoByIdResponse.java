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
        String observacoes,
        LocalDate dataEntrada,
        String estado,
        String cidade,
        String bairro,
        String rua,
        Integer numero,
        String complemento,
        String cep
) {
    public static AlunoByIdResponse daEntidade(Alunos aluno, Endereco endereco) {
        return new AlunoByIdResponse(
                aluno.getNome(),
                aluno.getDataNascimento(),
                aluno.getSexo(),
                aluno.getCpf().getCpf(),
                aluno.getTelefone(),
                aluno.getObservacoes(),
                aluno.getDataEntrada(),
                endereco.getCidade().getEstado().getNome(),
                endereco.getCidade().getNome(),
                endereco.getBairro(),
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getCep()
        );
    }

}
