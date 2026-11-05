package br.pucminas.sistema_hospitalar.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReagendamentoRequest(
        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @NotNull(message = "O horário é obrigatório")
        LocalTime horario) {
}
