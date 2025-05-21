package br.apae.ged.application.strategy;


import br.apae.ged.application.dto.user.UserRequestDTO;

public interface NewUserValidationStrategy {

    void validate(UserRequestDTO request);
}
