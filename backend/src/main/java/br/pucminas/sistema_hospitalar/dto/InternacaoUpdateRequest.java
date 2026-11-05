package br.pucminas.sistema_hospitalar.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InternacaoUpdateRequest(
        @NotNull(message = "A data prevista de alta é obrigatória")
        LocalDate dataPrevistaAlta,

        String observacoes) {
}
