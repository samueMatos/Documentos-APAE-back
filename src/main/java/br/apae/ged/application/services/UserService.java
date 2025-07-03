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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public UserLoginResponseDTO login(UserLoginDTO userLoginDetails) {

        // Verifica se o usuario login existe
        if (userLoginDetails.email().equals("admin@apae")) {

            if (userRepository.count() == 0) {

                UserRequestDTO userDTO = new UserRequestDTO(
                        "SUPER ADMIN",
                        "admin@apae",
                        "admin",
                        "admin",
                        1L
                );

                User user = UserRequestDTO.toEntity(userDTO);

                UserGroup group = userGroupRepository.findById(userDTO.groupId())
                        .orElseThrow(() -> new NotFoundException("Grupo não encontrado"));

                user.setUserGroup(group);
                
                user.setIsAtivo(true);

                userRepository.save(user);

            }

        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(userLoginDetails.email(), userLoginDetails.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var user = (User) auth.getPrincipal();
        var token = tokenService.generateToken(user);
        var expiresAt = LocalDateTime.now().plusMinutes(120);
        List<String> permissions = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new UserLoginResponseDTO(token, expiresAt, permissions);
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
