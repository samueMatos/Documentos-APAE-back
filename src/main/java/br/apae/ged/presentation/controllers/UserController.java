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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para autenticação e gerenciamento de usuários")
public class UserController {

    private final UserService service;
    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário", description = "Cria um novo usuário no sistema.")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequestDTO entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(entity));
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Autentica um usuário e retorna um token JWT para acesso às rotas protegidas.")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginDTO entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.login(entity));
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todos os usuários", description = "Retorna uma lista paginada de usuários, com filtro opcional por nome ou email.")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<Page<UserResponse>> listAll(
            Pageable pageable,
            @RequestParam(required = false) String termoBusca) {
        Page<UserResponse> users = service.listAll(pageable, termoBusca);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário por ID", description = "Retorna os detalhes de um usuário específico.")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        UserResponse user = service.findById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário", description = "Atualiza os dados de um usuário existente.")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody @Valid UserRequestDTO dto) {
        UserResponse updatedUser = service.update(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um usuário", description = "Remove permanentemente um usuário do sistema.")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) { // Adicionado @Valid (opcional)
        service.changeUserPassword(changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) {
        service.forgotPassword(forgotPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        service.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok().build();
    }

}


