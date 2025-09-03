package br.apae.ged.application.dto.documento_institucional;

import br.apae.ged.domain.models.DocumentoInstitucional;
import java.time.LocalDateTime;

public record DocumentoInstitucionalResponseDTO(
        Long id,
        String titulo,
        String tipoConteudo,
        LocalDateTime dataUpload,
        String uploadedBy) {
    public static DocumentoInstitucionalResponseDTO fromEntity(DocumentoInstitucional doc) {
        return new DocumentoInstitucionalResponseDTO(
                doc.getId(),
                doc.getTitulo(),
                doc.getTipoConteudo(),
                doc.getDataUpload(),
                doc.getUploadedBy() != null ? doc.getUploadedBy().getNome() : "N/A");
    }
}