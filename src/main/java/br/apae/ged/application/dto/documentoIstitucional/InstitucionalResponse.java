package br.apae.ged.application.dto.documentoIstitucional;

import java.time.LocalDateTime;

public record InstitucionalResponse(
        Long id,
        String titulo,
        String tipoConteudo,
        LocalDateTime dataUpload,
        LocalDateTime dataDocumento,
        String TipoDocumento
) {
}
