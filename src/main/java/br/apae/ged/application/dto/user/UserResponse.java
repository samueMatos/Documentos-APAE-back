package br.apae.ged.application.dto.user;


import br.apae.ged.domain.models.User;

public record UserResponse(Long id,
                           String nome,
                           String email) {

    public static UserResponse fromEntity(User user){
        if (user == null){
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getNome(),
                user.getUsername()
        );
    }
}
