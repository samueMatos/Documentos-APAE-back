package br.apae.ged.presentation.controllers;

import br.apae.ged.domain.models.UserGroup;
import br.apae.ged.application.services.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupo_usuario")
@PreAuthorize("hasAuthority('GRUPOS_PERMISSOES')")
@Tag(name = "Grupos de Usuários", description = "Endpoints para gerenciamento de grupos e suas permissões.")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @Operation(summary = "Lista todos os grupos de usuários", description = "Retorna uma lista com todos os grupos de usuários cadastrados e suas respectivas permissões.")
    @ApiResponse(responseCode = "200", description = "Lista de grupos retornada com sucesso.")
    @GetMapping("/list")
    public List<UserGroup> getAllGroups() {
        return userGroupService.findAll();
    }

    @Operation(summary = "Busca um grupo por ID", description = "Retorna os detalhes de um grupo de usuários específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grupo encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado.", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserGroup> getGroupById(
            @Parameter(description = "ID do grupo a ser buscado.") @PathVariable Long id) {
        UserGroup userGroup = userGroupService.findById(id);
        return ResponseEntity.ok(userGroup);
    }

    @Operation(summary = "Cria um novo grupo de usuários", description = "Cria um novo grupo e associa as permissões fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grupo criado com sucesso.")
    })
    @PostMapping("/create")
    public ResponseEntity<UserGroup> createGroup(@RequestBody UserGroup userGroup) {
        UserGroup newGroup = userGroupService.create(userGroup);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @Operation(summary = "Atualiza um grupo de usuários", description = "Atualiza o nome e/ou as permissões de um grupo existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grupo atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Grupo ou permissão não encontrado.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserGroup> updateGroup(
            @Parameter(description = "ID do grupo a ser atualizado.") @PathVariable Long id,
            @RequestBody UserGroup groupDetails) {
        UserGroup updatedGroup = userGroupService.update(id, groupDetails);
        return ResponseEntity.ok(updatedGroup);
    }

    @Operation(summary = "Exclui um grupo de usuários", description = "Remove um grupo do sistema com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Grupo excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado.", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "ID do grupo a ser excluído.") @PathVariable Long id) {
        userGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}