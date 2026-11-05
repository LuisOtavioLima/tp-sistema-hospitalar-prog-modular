package br.pucminas.sistema_hospitalar.model;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static br.pucminas.sistema_hospitalar.model.SituacaoConsulta.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SituacaoConsultaTest {

    private final Map<SituacaoConsulta, Set<SituacaoConsulta>> esperado = Map.of(
            AGENDADA, EnumSet.of(CONFIRMADA, CANCELADA, NAO_COMPARECEU),
            CONFIRMADA, EnumSet.of(EM_ATENDIMENTO, CANCELADA, NAO_COMPARECEU),
            EM_ATENDIMENTO, EnumSet.of(CONCLUIDA),
            CONCLUIDA, EnumSet.noneOf(SituacaoConsulta.class),
            CANCELADA, EnumSet.of(AGENDADA),
            NAO_COMPARECEU, EnumSet.of(AGENDADA));

    @Test
    void tabelaDeTransicoesCompleta() {
        for (SituacaoConsulta origem : values()) {
            for (SituacaoConsulta destino : values()) {
                assertEquals(esperado.get(origem).contains(destino), origem.podeMudarPara(destino),
                        origem + " -> " + destino);
            }
        }
    }

    @Test
    void concluidaNaoVoltaParaAgendada() {
        assertFalse(CONCLUIDA.podeMudarPara(AGENDADA));
    }

    @Test
    void canceladaNaoVaiDiretoParaConcluida() {
        assertFalse(CANCELADA.podeMudarPara(CONCLUIDA));
    }
}
