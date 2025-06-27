package br.apae.ged.application.dto.user;

import br.apae.ged.domain.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public record UserRequestDTO(String nome,
                             String email,
                             String username,
                             String password,
                             Long groupId) {

    public static User toEntity(UserRequestDTO request){
        return new User(
                request.nome(),
                request.email(),
                new BCryptPasswordEncoder().encode(request.password())
        );
    }
    public Long groupId() {
        return this.groupId;
    }
}
