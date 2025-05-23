package br.apae.ged.application.dto.document;

import br.apae.ged.domain.models.Document;
import br.apae.ged.domain.models.TipoDocumento;
import br.apae.ged.domain.models.enums.TipoArquivo;

import org.springframework.web.multipart.MultipartFile;

public record DocumentRequestDTO(
        String nome,
        TipoDocumento tipoDocumento,
        TipoArquivo tipoArquivo,
        Document prevVersion,
        MultipartFile file
) {
}
