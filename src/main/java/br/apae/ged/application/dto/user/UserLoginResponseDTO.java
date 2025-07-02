package br.apae.ged.application.dto.user;

import java.time.LocalDateTime;
import java.util.List;


public record UserLoginResponseDTO(String token, LocalDateTime expiresAt, List<String> permissions){
}
