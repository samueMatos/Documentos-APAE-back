package br.apae.ged.application.dto.aluno;

import br.apae.ged.domain.models.Alunos;
import br.apae.ged.domain.models.Cidade;
import br.apae.ged.domain.models.Endereco;
import br.apae.ged.domain.models.Estado;

import java.time.LocalDate;

public record AlunoRequestDTO(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String cpf,
        String telefone,
        String cpfResponsavel,
        String deficiencia,
        LocalDate dataEntrada,
        Boolean isAtivo,
        String observacoes,
        String cidade,
        String bairro,
        String rua,
        int numero,
        String complemento,
        String cep,
        String ibge
) {}
