package br.apae.ged.application.dto.document;

public record DocumentResponseStatusDTO(
        int statuscode,
        String message
) {
}
