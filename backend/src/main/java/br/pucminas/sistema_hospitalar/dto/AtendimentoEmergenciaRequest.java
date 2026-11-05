package br.pucminas.sistema_hospitalar.dto;

import br.pucminas.sistema_hospitalar.model.PrioridadeAtendimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtendimentoEmergenciaRequest(
        @NotNull(message = "O paciente é obrigatório")
        Long pacienteId,

        @NotBlank(message = "O motivo é obrigatório")
        String motivo,

        @NotNull(message = "A prioridade é obrigatória")
        PrioridadeAtendimento prioridade) {
}
