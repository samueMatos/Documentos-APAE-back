package br.apae.ged.application.dto.colaborador;

import br.apae.ged.domain.models.Colaborador;

public record ColaboradorResponseDTO(
        Long id,
        String nome,
        String cpf,
        String cargo) {
    public static ColaboradorResponseDTO fromEntity(Colaborador colaborador) {
        return new ColaboradorResponseDTO(
                colaborador.getId(),
                colaborador.getNome(),
                colaborador.getCpf().getCpf(),
                colaborador.getCargo());
    }
}