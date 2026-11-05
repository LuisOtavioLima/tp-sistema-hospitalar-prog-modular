package br.pucminas.sistema_hospitalar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaUpdateRequest(
        @NotNull(message = "O profissional é obrigatório")
        Long profissionalId,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @NotNull(message = "O horário é obrigatório")
        LocalTime horario,

        @NotBlank(message = "O motivo é obrigatório")
        String motivo) {
}
