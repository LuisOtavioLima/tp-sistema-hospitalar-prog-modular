package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AtendimentoEmergenciaTest {

    private final Paciente paciente = new Paciente(1L, "Maria", "52998224725", "maria@email.com", null, null);
    private final ProfissionalSaude profissional = new ProfissionalSaude(1L, "Ana", "CRM-1", "3199999", "ana@email.com") {
    };

    private AtendimentoEmergencia novoAtendimento(Paciente pacienteDoAtendimento, PrioridadeAtendimento prioridade) {
        return new AtendimentoEmergencia(LocalDate.now(), LocalTime.now(), pacienteDoAtendimento, "Dor no peito", prioridade);
    }

    @Test
    void atendimentoNovoAguardaAvaliacao() {
        assertEquals(SituacaoEmergencia.AGUARDANDO, novoAtendimento(paciente, PrioridadeAtendimento.ALTA).getSituacao());
    }

    @Test
    void fluxoCompletoAguardandoIniciadoConcluido() {
        AtendimentoEmergencia atendimento = novoAtendimento(paciente, PrioridadeAtendimento.CRITICA);

        atendimento.iniciar(profissional);
        assertEquals(SituacaoEmergencia.INICIADO, atendimento.getSituacao());
        assertSame(profissional, atendimento.getProfissional());

        atendimento.concluir();
        assertEquals(SituacaoEmergencia.CONCLUIDO, atendimento.getSituacao());
    }

    @Test
    void naoConcluiSemTerIniciado() {
        AtendimentoEmergencia atendimento = novoAtendimento(paciente, PrioridadeAtendimento.BAIXA);

        assertThrows(RegraNegocioException.class, atendimento::concluir);
        assertEquals(SituacaoEmergencia.AGUARDANDO, atendimento.getSituacao());
    }

    @Test
    void naoIniciaDuasVezes() {
        AtendimentoEmergencia atendimento = novoAtendimento(paciente, PrioridadeAtendimento.BAIXA);
        atendimento.iniciar(profissional);

        assertThrows(RegraNegocioException.class, () -> atendimento.iniciar(profissional));
    }

    @Test
    void naoIniciaSemProfissional() {
        AtendimentoEmergencia atendimento = novoAtendimento(paciente, PrioridadeAtendimento.MODERADA);

        assertThrows(RegraNegocioException.class, () -> atendimento.iniciar(null));
        assertEquals(SituacaoEmergencia.AGUARDANDO, atendimento.getSituacao());
    }

    @Test
    void naoIniciaSemPrioridade() {
        AtendimentoEmergencia atendimento = novoAtendimento(paciente, null);

        assertThrows(RegraNegocioException.class, () -> atendimento.iniciar(profissional));
        assertEquals(SituacaoEmergencia.AGUARDANDO, atendimento.getSituacao());
    }

    @Test
    void naoIniciaSemPaciente() {
        AtendimentoEmergencia atendimento = novoAtendimento(null, PrioridadeAtendimento.ALTA);

        assertThrows(RegraNegocioException.class, () -> atendimento.iniciar(profissional));
    }
}
