package br.apae.ged.application.dto.senha;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {

    @NotBlank(message = "O email não pode ser vazio.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @NotBlank(message = "O código de recuperação não pode ser vazio.")
    private String recoveryCode;

    @NotBlank(message = "A nova senha não pode ser vazia.")
    @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres.")
    private String newPassword;
}