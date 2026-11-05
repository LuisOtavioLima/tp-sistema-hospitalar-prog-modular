package br.pucminas.sistema_hospitalar.dto;

import jakarta.validation.constraints.NotNull;

public record IniciarEmergenciaRequest(
        @NotNull(message = "O profissional é obrigatório")
        Long profissionalId) {
}
