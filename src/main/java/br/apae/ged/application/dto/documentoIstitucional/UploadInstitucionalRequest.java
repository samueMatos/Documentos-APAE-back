package br.apae.ged.application.dto.documentoIstitucional;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UploadInstitucionalRequest(
        MultipartFile documento,
        String nome,
        LocalDate dataCriacao,
        String tipoDocumento
) {
}
