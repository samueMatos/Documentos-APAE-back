package br.apae.ged.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Tag(name = "Saúde da Aplicação", description = "Endpoints para verificação do status e saúde da API.")
public class HealthChecker {

    @Operation(summary = "Verifica a saúde da aplicação (Checker 1)", description = "Endpoint simples para verificar se a aplicação está online e respondendo. Retorna a string 'OK'.")
    @ApiResponse(responseCode = "200", description = "Aplicação está operacional.", content = @Content(mediaType = "text/plain"))
    @GetMapping("/checker1")
    public String checker() {
        return "OK";
    }

    @Operation(summary = "Verifica a saúde da aplicação (Checker 2)", description = "Endpoint alternativo para verificação de saúde. Retorna a string 'OK 2'.")
    @ApiResponse(responseCode = "200", description = "Aplicação está operacional.", content = @Content(mediaType = "text/plain"))
    @GetMapping("/checker2")
    public String checker2() {
        return "OK 2";
    }
}