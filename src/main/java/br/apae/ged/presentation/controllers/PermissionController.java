package br.apae.ged.presentation.controllers;

import br.apae.ged.domain.models.Permission;
import br.apae.ged.application.services.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/permissoes")
@RequiredArgsConstructor
@Tag(name = "Permissões", description = "Endpoints para gerenciamento de permissões de acesso.")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Lista todas as permissões disponíveis", description = "Retorna uma lista com todas as permissões cadastradas no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de permissões retornada com sucesso.")
    @GetMapping
    @PreAuthorize("hasAuthority('GRUPOS_PERMISSOES')")
    public List<Permission> getAllPermissions() {
        return permissionService.findAll();
    }

    @Operation(summary = "Cria uma nova permissão (Apenas Super Admin)", description = "Cria uma nova permissão de acesso no sistema. Requer privilégios de Super Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permissão criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Permissão com o mesmo nome já existe.", content = @Content)
    })
    @PostMapping("/admin/create")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        Permission createdPermission = permissionService.create(permission);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    @Operation(summary = "Exclui uma permissão (Apenas Super Admin)", description = "Remove uma permissão do sistema com base no ID. Requer privilégios de Super Administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permissão excluída com sucesso."),
            @ApiResponse(responseCode = "404", description = "Permissão não encontrada.", content = @Content)
    })
    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}