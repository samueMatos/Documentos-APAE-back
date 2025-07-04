package br.apae.ged.application.dto.user;

import br.apae.ged.domain.models.User;
import br.apae.ged.domain.models.UserGroup;


public record UserResponse(
        Long id,
        String nome,
        String email,
        UserGroupResponse userGroup
) {

    public record UserGroupResponse(
            Long id,
            String nome
    ) {
        public static UserGroupResponse fromEntity(UserGroup group) {

            if (group == null) {
                return null;
            }
            return new UserGroupResponse(group.getId(), group.getNome());
        }
    }


    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                UserGroupResponse.fromEntity(user.getUserGroup())
        );
    }
}