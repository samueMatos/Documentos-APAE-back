package br.apae.ged.application.dto.user;

import java.time.LocalDateTime;

public record UserLoginResponseDTO(
        String token,
        LocalDateTime expiresAt
) {
}
