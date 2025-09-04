package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.ChangePasswordDTO;
import br.apae.ged.application.dto.senha.ForgotPasswordDTO;
import br.apae.ged.application.dto.senha.ResetPasswordDTO;
import br.apae.ged.application.dto.user.UserLoginDTO;
import br.apae.ged.application.dto.user.UserLoginResponseDTO;
import br.apae.ged.application.dto.user.UserRequestDTO;
import br.apae.ged.application.dto.user.UserResponse;
import br.apae.ged.application.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para autenticação e gerenciamento de usuários.")
public class UserController {

    private final UserService service;

    @Operation(summary = "Registra um novo usuário", description = "Cria um novo usuário no sistema. Requer permissão 'GERENCIAR_USUARIO'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Email já está em uso.", content = @Content)
    })
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequestDTO entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(entity));
    }

    @Operation(summary = "Autentica um usuário", description = "Realiza o login do usuário e retorna um token JWT para acesso às rotas protegidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Login bem-sucedido."),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou usuário inativo.", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginDTO entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.login(entity));
    }

    @Operation(summary = "Lista todos os usuários", description = "Retorna uma lista paginada de usuários, com filtro opcional por nome ou email. Requer permissão 'GERENCIAR_USUARIO'.")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso.")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<Page<UserResponse>> listAll(
            Pageable pageable,
            @Parameter(description = "Termo para busca por nome ou email.") @RequestParam(required = false) String termoBusca) {
        Page<UserResponse> users = service.listAll(pageable, termoBusca);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Busca um usuário por ID", description = "Retorna os detalhes de um usuário específico. Requer permissão 'GERENCIAR_USUARIO'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        UserResponse user = service.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Atualiza um usuário", description = "Atualiza os dados de um usuário existente. Requer permissão 'GERENCIAR_USUARIO'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody @Valid UserRequestDTO dto) {
        UserResponse updatedUser = service.update(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Exclui um usuário", description = "Remove permanentemente um usuário do sistema. Requer permissão 'GERENCIAR_USUARIO'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Altera a senha do usuário autenticado", description = "Permite que o usuário logado altere sua própria senha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida.", content = @Content)
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        service.changeUserPassword(changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Solicita a recuperação de senha", description = "Envia um código de recuperação para o email do usuário, caso ele exista.")
    @ApiResponse(responseCode = "200", description = "Se o email existir, um código de recuperação será enviado.")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) {
        service.forgotPassword(forgotPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Redefine a senha com código de recuperação", description = "Permite a redefinição de senha utilizando o código enviado por email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso."),
            @ApiResponse(responseCode = "400", description = "Código de recuperação inválido ou expirado.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        service.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok().build();
    }
}