package br.apae.ged.application.dto.documentoIstitucional;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record InstitucionalRequest(
        MultipartFile conteudo,
        String tipoDocumento,
        LocalDate dataDocumento
) {
}
