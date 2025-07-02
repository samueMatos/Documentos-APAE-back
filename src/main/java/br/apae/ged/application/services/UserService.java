package br.apae.ged.application.services;

import br.apae.ged.application.dto.ChangePasswordDTO;
import br.apae.ged.domain.utils.AuthenticationUtil;
import br.apae.ged.presentation.configs.TokenService;
import br.apae.ged.application.dto.user.UserLoginDTO;
import br.apae.ged.application.dto.user.UserLoginResponseDTO;
import br.apae.ged.application.dto.user.UserRequestDTO;
import br.apae.ged.application.dto.user.UserResponse;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.domain.models.User;
import br.apae.ged.domain.repositories.UserRepository;
import br.apae.ged.domain.models.UserGroup;
import br.apae.ged.domain.repositories.UserGroupRepository;
import br.apae.ged.application.strategy.NewUserValidationStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final List<NewUserValidationStrategy> userValidationStrategies;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(UserRequestDTO entity) {

        userValidationStrategies.forEach(validation -> validation.validate(entity));

        User user = UserRequestDTO.toEntity(entity);
        UserGroup group = userGroupRepository.findById(entity.groupId())
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
        user.setUserGroup(group);
        user.setIsAtivo(true);
        var save = userRepository.save(user);
        return UserResponse.fromEntity(save);
    }

    public UserLoginResponseDTO login(UserLoginDTO user) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(user.email(), user.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        return new UserLoginResponseDTO(tokenService.generateToken((User) auth.getPrincipal()), LocalDateTime.now().plusMinutes(120));
    }

    public void changeStatusUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (!user.getIsAtivo()) {
            user.setIsAtivo(true);
            userRepository.save(user);
            return;
        }

        user.setIsAtivo(false);

        userRepository.save(user);
    }

    public List<String> getAllGroups() {
        return userGroupRepository.findAll().stream()
                .map(UserGroup::getNome)
                .toList();
    }

    @Transactional //ALTERADO ERICK
    public void changeUserPassword(ChangePasswordDTO dto) {
        // 1. Obter o usuário autenticado
        User authenticatedUser = AuthenticationUtil.retriveAuthenticatedUser();
        if (authenticatedUser == null || authenticatedUser.getEmail() == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }

        User userToUpdate = userRepository.findByEmail(authenticatedUser.getEmail());
        if (userToUpdate == null) {
            throw new RuntimeException("Usuário não encontrado no banco de dados: " + authenticatedUser.getEmail());
        }

        if (!passwordEncoder.matches(dto.getSenhaAtual(), userToUpdate.getPassword())) {
            throw new RuntimeException("A senha atual está incorreta.");
        }

        String novaSenhaCodificada = passwordEncoder.encode(dto.getNovaSenha());
        userToUpdate.setPassword(novaSenhaCodificada);

        userRepository.save(userToUpdate);


    }
}
