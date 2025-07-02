package br.apae.ged.presentation.controllers;

import br.apae.ged.application.dto.ChangePasswordDTO;
import br.apae.ged.application.dto.user.UserLoginDTO;
import br.apae.ged.application.dto.user.UserLoginResponseDTO;
import br.apae.ged.application.dto.user.UserRequestDTO;
import br.apae.ged.application.dto.user.UserResponse;
import br.apae.ged.application.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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



    @DeleteMapping("/{id}")
    @Operation(summary = "Ativa/Desativa um usuário", description = "Altera o status de um usuário para ativo ou inativo.")
    @PreAuthorize("hasAuthority('GERENCIAR_USUARIO')")
    public ResponseEntity<Void> desativarUser(@PathVariable("id") Long id) {
        service.changeStatusUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password") //ALTERADO ERICK
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) { // Adicionado @Valid (opcional)
        service.changeUserPassword(changePasswordDTO);
        return ResponseEntity.ok().build();
    }

}

