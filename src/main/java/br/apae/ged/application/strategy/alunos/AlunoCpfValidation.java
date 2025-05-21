package br.apae.ged.application.strategy.alunos;

import br.apae.ged.application.dto.aluno.AlunoRequestDTO;
import br.apae.ged.application.exceptions.ValidationException;
import br.apae.ged.domain.repositories.AlunoRepository;
import br.apae.ged.application.strategy.NewAlunoValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlunoCpfValidation implements NewAlunoValidationStrategy {

    private final AlunoRepository alunoRepository;

    @Override
    public void validate(AlunoRequestDTO request) {
        if (alunoRepository.existsByCpf(request.cpf())){
            throw new ValidationException("CPF já cadastrado");
        }
    }
}
