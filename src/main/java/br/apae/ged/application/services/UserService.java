package br.apae.ged.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.apae.ged.application.dto.ChangePasswordDTO;
import br.apae.ged.application.dto.senha.ForgotPasswordDTO;
import br.apae.ged.application.dto.senha.ResetPasswordDTO;
import br.apae.ged.application.dto.user.UserLoginDTO;
import br.apae.ged.application.dto.user.UserLoginResponseDTO;
import br.apae.ged.application.dto.user.UserRequestDTO;
import br.apae.ged.application.dto.user.UserResponse;
import br.apae.ged.application.exceptions.NotFoundException;
import br.apae.ged.application.strategy.NewUserValidationStrategy;
import br.apae.ged.domain.models.User;
import br.apae.ged.domain.models.UserGroup;
import br.apae.ged.domain.repositories.UserGroupRepository;
import br.apae.ged.domain.repositories.UserRepository;
import br.apae.ged.domain.utils.AuthenticationUtil;
import br.apae.ged.presentation.configs.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final EmailService emailService;
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

    public Page<UserResponse> listAll(Pageable pageable, String termoBusca) {
        Page<User> userPage;
        if (termoBusca != null && !termoBusca.isBlank()) {
            userPage = userRepository.findByTermoBusca(termoBusca, pageable);
        } else {
            userPage = userRepository.findAllWithGroups(pageable);
        }
        return userPage.map(UserResponse::fromEntity);
    }


    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário com ID " + id + " não encontrado."));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse update(Long id, UserRequestDTO dto) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário com ID " + id + " não encontrado para atualização."));


        userToUpdate.setNome(dto.nome());
        userToUpdate.setEmail(dto.email());


        if (dto.groupId() != null) {
            UserGroup group = userGroupRepository.findById(dto.groupId())
                    .orElseThrow(() -> new NotFoundException("Grupo com ID " + dto.groupId() + " não encontrado."));
            userToUpdate.setUserGroup(group);
        }


        if (dto.password() != null && !dto.password().isBlank()) {
            userToUpdate.setPassword(passwordEncoder.encode(dto.password()));
        }

        User updatedUser = userRepository.save(userToUpdate);
        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Usuário com ID " + id + " não encontrado para exclusão.");
        }
        userRepository.deleteById(id);
    }

    // SERVIÇO DE RECUPERAÇÃO DE SENHA

    @Transactional
    public void changeUserPassword(ChangePasswordDTO dto) {
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

    @Transactional
    public void forgotPassword(ForgotPasswordDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {

            return;
        }

        String recoveryCode = generateRandomCode(6);
        user.setRecoveryCode(recoveryCode);
        user.setRecoveryCodeExpiration(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        String subject = "Código de Recuperação de Senha";
        String html = """
                        <html>
                        <body>
                            <img src="https://i.imgur.com/OOWZXHM.png" alt="Logo" width="120"/>
                            <h2>Código para Recuperação de Senha</h2>
                            <p>Prezado(a) usuário(a),</p>
                            <p>Recebemos uma solicitação para redefinir a senha da sua conta. Para continuar, utilize o código de verificação a seguir:</p>
                            <p style="font-size: 24px; font-weight: bold; text-align: center;">%s</p>
                            <p>Por favor, insira este código no campo indicado na página de recuperação de senha.</p>
                            <p>Este código de segurança é válido por apenas 10 minutos. Por motivos de segurança, não o compartilhe com ninguém.</p>
                            <p>Se você não fez essa solicitação, ignore este e-mail. Sua senha atual permanecerá inalterada.</p>
                            <p>Atenciosamente,<br> APAE</p>
                        </body>
                        </html>
                    """.formatted(recoveryCode);

emailService.sendHtmlEmail(user.getEmail(), subject, html);
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());

        if (user == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        if (user.getRecoveryCode() == null || !user.getRecoveryCode().equals(dto.getRecoveryCode())) {
            throw new RuntimeException("Código de recuperação inválido.");
        }

        if (user.getRecoveryCodeExpiration() == null || user.getRecoveryCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código de recuperação expirado.");
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new RuntimeException("A nova senha deve ter pelo menos 6 caracteres.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setRecoveryCode(null);
        user.setRecoveryCodeExpiration(null);
        userRepository.save(user);
    }

    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

}

