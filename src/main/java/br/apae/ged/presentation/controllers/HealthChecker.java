package br.apae.ged.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@Tag(name = "Saúde", description = "Endpoints para verificação de saúde da aplicação")
public class HealthChecker {

    @GetMapping("/checker1")
    @Operation(summary = "Verificador de saúde 1", description = "Este endpoint deve retornar uma String escrito 'OK'")
    public String checker() {
        return "OK";
    }

    @GetMapping("/checker2")
    @Operation(summary = "Verificador de saúde 2", description = "Este endpoint deve retornar uma String escrito 'OK 2'")
    public String checker2() {
        return "OK 2";
    }
}