package br.apae.ged.application.dto.document;

import br.apae.ged.domain.models.Document;
import br.apae.ged.domain.models.TipoDocumento;
import br.apae.ged.domain.models.enums.TipoArquivo;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record DocumentRequestDTO(
        String tipoDocumento,
        MultipartFile file,
        LocalDate dataDocumento
) {
}
