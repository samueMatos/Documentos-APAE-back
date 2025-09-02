package br.apae.ged.application.dto.documentoIstitucional;

import br.apae.ged.domain.models.Institucional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

public record InstucionalResponse(
        Long id,
        String titulo,
        String tipoConteudo,
        String doc,
        LocalDate dataCriacao,
        LocalDateTime dataUpload,
        LocalDateTime dataDownload,
        String tipoDocumento
) {
    public InstucionalResponse(Institucional doc) {
        this(
                doc.getId(),
                doc.getTitulo(),
                doc.getTipoConteudo(),
                doc.getConteudo(),
                doc.getDataDocumento(),
                doc.getDataUpload(),
                doc.getDataDownload(),
                doc.getTipoDocumento() != null ? doc.getTipoDocumento().getNome() : null
        );
    }


}
