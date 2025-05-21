package br.apae.ged.application.strategy;


import br.apae.ged.application.dto.aluno.AlunoRequestDTO;

public interface NewAlunoValidationStrategy{
    void validate(AlunoRequestDTO request);
}
