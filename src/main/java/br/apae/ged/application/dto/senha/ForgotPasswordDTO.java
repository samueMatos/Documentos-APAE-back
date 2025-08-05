package br.apae.ged.application.dto.senha;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordDTO {

    @NotBlank(message = "O email não pode ser vazio.")
    @Email(message = "Formato de email inválido.")
    private String email;
}