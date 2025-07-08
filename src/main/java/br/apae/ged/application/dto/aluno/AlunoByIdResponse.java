package br.apae.ged.application.dto.aluno;

import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Endereco;

import java.time.LocalDate;


public record AlunoByIdResponse(

        String nome,
        LocalDate dataNascimento,
        String sexo,
        String cpf,
        String matricula,
        String telefone,
        LocalDate dataEntrada,
        String observacoes,


        String cep,
        String rua,
        int numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String ibge
) {


    public static AlunoByIdResponse daEntidade(Alunos aluno, Endereco endereco) {
        if (aluno == null) {
            return null;
        }


        String cep = endereco != null ? endereco.getCep() : "";
        String rua = endereco != null ? endereco.getRua() : "";
        int numero = endereco != null ? endereco.getNumero() : 0;
        String complemento = endereco != null ? endereco.getComplemento() : "";
        String bairro = endereco != null ? endereco.getBairro() : "";
        String cidadeNome = (endereco != null && endereco.getCidade() != null) ? endereco.getCidade().getNome() : "";
        String ibge = (endereco != null && endereco.getCidade() != null) ? endereco.getCidade().getIbge() : "";


        String estadoUf = (endereco != null && endereco.getCidade() != null && endereco.getCidade().getEstado() != null)
                ? endereco.getCidade().getEstado().getUf()
                : "";

        return new AlunoByIdResponse(
                aluno.getNome(),
                aluno.getDataNascimento(),
                aluno.getSexo(),
                aluno.getCpf() != null ? aluno.getCpf().getCpf() : "",
                aluno.getMatricula(),
                aluno.getTelefone(),
                aluno.getDataEntrada(),
                aluno.getObservacoes(),
                cep,
                rua,
                numero,
                complemento,
                bairro,
                cidadeNome,
                estadoUf,
                ibge
        );
    }
}