package br.apae.ged.application.dto.documentoIstitucional;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record AtualizarInstitucionalRequest(
        String nome,
        MultipartFile file,
        LocalDate dataCriacao,
        String tipoDocumento
) {
}
