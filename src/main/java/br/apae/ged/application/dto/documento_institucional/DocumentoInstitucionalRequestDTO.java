package br.apae.ged.application.dto.documento_institucional;

import org.springframework.web.multipart.MultipartFile;

public record DocumentoInstitucionalRequestDTO(
        String titulo,
        MultipartFile file) {
}