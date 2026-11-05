package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaRotinaTest {

    private final Paciente paciente = new Paciente(1L, "Maria", "52998224725", "maria@email.com", null, null);
    private final ProfissionalSaude profissional = new ProfissionalSaude(1L, "Ana", "CRM-1", "3199999", "ana@email.com") {
    };
    private final LocalDate data = LocalDate.of(2026, 12, 10);
    private final LocalTime horario = LocalTime.of(14, 0);

    private ConsultaRotina novaConsulta() {
        return new ConsultaRotina(data, horario, paciente, profissional, "Check-up anual");
    }

    @Test
    void consultaNovaComecaAgendada() {
        ConsultaRotina consulta = novaConsulta();
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
        assertEquals(TipoConsulta.ROTINA, consulta.getTipo());
        assertTrue(consulta.isEditavel());
    }

    @Test
    void fluxoCompletoAteConcluir() {
        ConsultaRotina consulta = novaConsulta();

        consulta.confirmar();
        consulta.iniciar();
        consulta.concluir("Paciente saudável");

        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
        assertEquals("Paciente saudável", consulta.getObservacoes());
        assertFalse(consulta.isEditavel());
    }

    @Test
    void naoIniciaSemConfirmar() {
        ConsultaRotina consulta = novaConsulta();
        assertThrows(RegraNegocioException.class, consulta::iniciar);
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
    }

    @Test
    void naoConcluiSemIniciar() {
        ConsultaRotina consulta = novaConsulta();
        consulta.confirmar();
        assertThrows(RegraNegocioException.class, () -> consulta.concluir(null));
        assertEquals(SituacaoConsulta.CONFIRMADA, consulta.getSituacao());
    }

    @Test
    void concluidaNaoPodeSerReagendadaNemCancelada() {
        ConsultaRotina consulta = novaConsulta();
        consulta.confirmar();
        consulta.iniciar();
        consulta.concluir(null);

        assertThrows(RegraNegocioException.class, () -> consulta.reagendar(data.plusDays(1), horario));
        assertThrows(RegraNegocioException.class, consulta::cancelar);
        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
        assertEquals(data, consulta.getData());
    }

    @Test
    void canceladaSoConcluiDepoisDeReagendar() {
        ConsultaRotina consulta = novaConsulta();
        consulta.cancelar();

        assertThrows(RegraNegocioException.class, () -> consulta.concluir(null));
        assertThrows(RegraNegocioException.class, consulta::iniciar);

        consulta.reagendar(data.plusDays(7), LocalTime.of(9, 30));
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
        assertEquals(data.plusDays(7), consulta.getData());

        consulta.confirmar();
        consulta.iniciar();
        consulta.concluir(null);
        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
    }

    @Test
    void faltaPodeSerReagendada() {
        ConsultaRotina consulta = novaConsulta();
        consulta.confirmar();
        consulta.registrarNaoComparecimento();
        assertFalse(consulta.ocupaAgenda());

        consulta.reagendar(data.plusDays(2), horario);
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
        assertTrue(consulta.ocupaAgenda());
    }

    @Test
    void naoReagendaConsultaQueAindaEstaAgendada() {
        assertThrows(RegraNegocioException.class, () -> novaConsulta().reagendar(data.plusDays(1), horario));
    }

    @Test
    void observacoesSoDuranteOuDepoisDoAtendimento() {
        ConsultaRotina consulta = novaConsulta();
        assertThrows(RegraNegocioException.class, () -> consulta.registrarObservacoes("Cedo demais"));

        consulta.confirmar();
        consulta.iniciar();
        consulta.registrarObservacoes("Pressão 12x8");
        assertEquals("Pressão 12x8", consulta.getObservacoes());

        consulta.concluir(null);
        assertEquals("Pressão 12x8", consulta.getObservacoes());
        assertThrows(RegraNegocioException.class, () -> consulta.registrarObservacoes(" "));
    }

    @Test
    void exigeMotivoEPaciente() {
        assertThrows(RegraNegocioException.class,
                () -> new ConsultaRotina(data, horario, paciente, profissional, " ").validarCondicoes());
        assertThrows(RegraNegocioException.class,
                () -> new ConsultaRotina(data, horario, null, profissional, "Dor").validarCondicoes());
        assertThrows(RegraNegocioException.class,
                () -> new ConsultaRotina(data, horario, paciente, null, "Dor").validarCondicoes());
    }

    @Test
    void atualizaAgendamentoSomenteEnquantoEditavel() {
        ConsultaRotina consulta = novaConsulta();
        consulta.atualizarAgendamento(profissional, data.plusDays(1), LocalTime.of(10, 0), "Retorno");
        assertEquals("Retorno", consulta.getMotivo());
        assertEquals(data.plusDays(1), consulta.getData());

        consulta.cancelar();
        assertThrows(RegraNegocioException.class,
                () -> consulta.atualizarAgendamento(profissional, data, horario, "Outro"));
        assertEquals("Retorno", consulta.getMotivo());
    }

    @Test
    void resumoVaiParaOHistorico() {
        ConsultaRotina consulta = novaConsulta();
        consulta.confirmar();
        consulta.iniciar();
        consulta.concluir("Tudo certo");

        String resumo = consulta.resumo();
        assertTrue(resumo.startsWith("Consulta de rotina em 2026-12-10"));
        assertTrue(resumo.contains("Ana"));
        assertTrue(resumo.endsWith("Observações: Tudo certo"));
    }
}
