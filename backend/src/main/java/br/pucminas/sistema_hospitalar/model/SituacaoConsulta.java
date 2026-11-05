package br.pucminas.sistema_hospitalar.model;

import java.util.EnumSet;
import java.util.Set;

public enum SituacaoConsulta {
    AGENDADA,
    CONFIRMADA,
    EM_ATENDIMENTO,
    CONCLUIDA,
    CANCELADA,
    NAO_COMPARECEU;

    public Set<SituacaoConsulta> proximasPermitidas() {
        return switch (this) {
            case AGENDADA -> EnumSet.of(CONFIRMADA, CANCELADA, NAO_COMPARECEU);
            case CONFIRMADA -> EnumSet.of(EM_ATENDIMENTO, CANCELADA, NAO_COMPARECEU);
            case EM_ATENDIMENTO -> EnumSet.of(CONCLUIDA);
            // só sai daqui com um novo agendamento
            case CANCELADA, NAO_COMPARECEU -> EnumSet.of(AGENDADA);
            case CONCLUIDA -> EnumSet.noneOf(SituacaoConsulta.class);
        };
    }

    public boolean podeMudarPara(SituacaoConsulta nova) {
        return proximasPermitidas().contains(nova);
    }
}
