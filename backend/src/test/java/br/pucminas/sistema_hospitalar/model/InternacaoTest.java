package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InternacaoTest {

    private static final LocalDate ENTRADA = LocalDate.of(2026, 11, 1);
    private static final LocalDate ALTA_PREVISTA = LocalDate.of(2026, 11, 5);

    private Quarto quarto;
    private Internacao internacao;

    @BeforeEach
    void preparar() {
        quarto = new Quarto(101, 1, 1, TipoQuarto.ENFERMARIA);
        internacao = novaInternacao(quarto);
    }

    private Internacao novaInternacao(Quarto quartoDaInternacao) {
        Paciente paciente = new Paciente(1L, "Maria", "52998224725", "maria@email.com", null, null);
        ProfissionalSaude profissional = new ProfissionalSaude(1L, "Ana", "CRM-1", "3199999", "ana@email.com") {
        };
        return new Internacao(paciente, profissional, quartoDaInternacao, ENTRADA, ALTA_PREVISTA, null);
    }

    @Test
    void criarInternacaoNaoOcupaVagaAntesDaEntrada() {
        assertEquals(0, quarto.getOcupacaoAtual());
        assertFalse(internacao.isAtiva());
    }

    @Test
    void registrarEntradaOcupaAVaga() {
        internacao.registrarEntrada();

        assertEquals(1, quarto.getOcupacaoAtual());
        assertTrue(internacao.isAtiva());
    }

    @Test
    void entradaNaoPodeSerRegistradaDuasVezes() {
        Quarto quartoGrande = new Quarto(102, 1, 4, TipoQuarto.ENFERMARIA);
        Internacao outra = novaInternacao(quartoGrande);
        outra.registrarEntrada();

        assertThrows(RegraNegocioException.class, outra::registrarEntrada);
        assertEquals(1, quartoGrande.getOcupacaoAtual());
    }

    @Test
    void entradaEmQuartoLotadoEhRejeitadaENaoAtivaAInternacao() {
        internacao.registrarEntrada();
        Internacao outra = novaInternacao(quarto);

        assertThrows(RegraNegocioException.class, outra::registrarEntrada);
        assertFalse(outra.isAtiva());
    }

    @Test
    void registrarAltaLiberaAVaga() {
        internacao.registrarEntrada();

        internacao.registrarAlta(ALTA_PREVISTA, "Sem intercorrências");

        assertEquals(0, quarto.getOcupacaoAtual());
        assertEquals(ALTA_PREVISTA, internacao.getDataEfetivaAlta());
        assertEquals("Sem intercorrências", internacao.getObservacoesAlta());
        assertFalse(internacao.isAtiva());
    }

    @Test
    void altaNoMesmoDiaDaEntradaEhPermitida() {
        internacao.registrarEntrada();

        internacao.registrarAlta(ENTRADA, null);

        assertEquals(ENTRADA, internacao.getDataEfetivaAlta());
    }

    @Test
    void altaAnteriorAEntradaEhRejeitadaENaoLiberaAVaga() {
        internacao.registrarEntrada();

        assertThrows(RegraNegocioException.class, () -> internacao.registrarAlta(ENTRADA.minusDays(1), null));
        assertEquals(1, quarto.getOcupacaoAtual());
        assertTrue(internacao.isAtiva());
    }

    @Test
    void altaSemEntradaEhRejeitada() {
        assertThrows(RegraNegocioException.class, () -> internacao.registrarAlta(ALTA_PREVISTA, null));
    }

    @Test
    void altaNaoPodeSerRegistradaDuasVezes() {
        internacao.registrarEntrada();
        internacao.registrarAlta(ALTA_PREVISTA, null);

        assertThrows(RegraNegocioException.class, () -> internacao.registrarAlta(ALTA_PREVISTA, null));
        assertEquals(0, quarto.getOcupacaoAtual());
    }

    @Test
    void altaPrevistaAnteriorAEntradaEhRejeitada() {
        assertThrows(RegraNegocioException.class, () -> internacao.atualizar(ENTRADA.minusDays(1), null));
        assertEquals(ALTA_PREVISTA, internacao.getDataPrevistaAlta());
    }
}
