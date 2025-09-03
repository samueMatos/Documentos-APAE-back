package br.apae.ged.application.dto.document;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record DocumentRequestDTO(
        String tipoDocumento,
        MultipartFile file,
        LocalDate dataDocumento
) {
}
