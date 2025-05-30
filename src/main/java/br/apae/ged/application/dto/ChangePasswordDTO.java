package br.apae.ged.application.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class ChangePasswordDTO {

    @NotBlank(message = "A senha atual não pode ser vazia.")
    private String senhaAtual;

    @NotBlank(message = "A nova senha não pode ser vazia.")
    @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres.")
    private String novaSenha;
}