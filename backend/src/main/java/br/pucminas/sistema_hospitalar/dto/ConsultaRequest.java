package br.pucminas.sistema_hospitalar.dto;

import br.pucminas.sistema_hospitalar.model.TipoConsulta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaRequest(
        @NotNull(message = "O tipo da consulta é obrigatório")
        TipoConsulta tipo,

        @NotNull(message = "O paciente é obrigatório")
        Long pacienteId,

        @NotNull(message = "O profissional é obrigatório")
        Long profissionalId,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @NotNull(message = "O horário é obrigatório")
        LocalTime horario,

        @NotBlank(message = "O motivo é obrigatório")
        String motivo,

        // só usado na consulta especializada
        String especialidade) {
}
