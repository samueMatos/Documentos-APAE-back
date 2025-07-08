package br.apae.ged.application.dto.aluno;

import java.time.LocalDate;

public record AlunoRequestDTO(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String cpf,
        String matricula,
        String telefone,
        LocalDate dataEntrada,
        Boolean isAtivo,
        String observacoes,
        String cidade,
        String bairro,
        String rua,
        int numero,
        String complemento,
        String cep,
        String ibge,
        String laudo
) {}
