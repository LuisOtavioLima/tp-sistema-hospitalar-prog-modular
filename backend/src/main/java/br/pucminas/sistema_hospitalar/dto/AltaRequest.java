package br.pucminas.sistema_hospitalar.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AltaRequest(
        @NotNull(message = "A data efetiva de alta é obrigatória")
        LocalDate data,

        String observacoes) {
}
