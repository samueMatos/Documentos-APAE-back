package br.apae.ged.application.dto.document;

import br.apae.ged.application.dto.aluno.AlunoResponseDTO;
import br.apae.ged.application.dto.user.UserResponse;
import br.apae.ged.domain.models.Document;
import br.apae.ged.domain.models.TipoDocumento;
import br.apae.ged.domain.models.enums.TipoArquivo;

import java.time.LocalDateTime;

public record DocumentResponseDTO(
        Long id,
        String nome,
        TipoDocumento tipoDocumento,
        byte[] documento,
        String tipoConteudo,
        LocalDateTime dataUpload,
        LocalDateTime dataDownload
) {

    public static DocumentResponseDTO fromEntity(Document document, byte[] documento){
        return new DocumentResponseDTO(
                document.getId(),
                document.getTitulo(),
                document.getTipoDocumento(),
                documento,
                document.getTipoConteudo(),
                document.getDataUpload(),
                document.getDataDownload()
        );
    }
    public static DocumentResponseDTO fromEntityWithoutContent(Document document) {
                return new DocumentResponseDTO(
                document.getId(),
                document.getTitulo(),
                document.getTipoDocumento(),
                null,
                document.getTipoConteudo(),
                document.getDataUpload(),
                document.getDataDownload()

        );
    }
}
