package br.pucminas.sistema_hospitalar.dto;

import br.pucminas.sistema_hospitalar.model.TipoQuarto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record QuartoRequest(
        @NotNull(message = "O número de identificação é obrigatório")
        @Positive(message = "O número de identificação deve ser positivo")
        Integer numeroIdentificacao,

        @NotNull(message = "O andar é obrigatório")
        @PositiveOrZero(message = "O andar não pode ser negativo")
        Integer andar,

        @NotNull(message = "A capacidade máxima é obrigatória")
        @Positive(message = "A capacidade máxima deve ser de pelo menos 1 paciente")
        Integer capacidadeMaxima,

        @NotNull(message = "O tipo do quarto é obrigatório")
        TipoQuarto tipo) {
}
