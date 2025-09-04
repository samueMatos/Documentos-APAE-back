package br.apae.ged.application.dto.colaborador;

public record ColaboradorRequestDTO(
        String nome,
        String cpf,
        String cargo) {
}