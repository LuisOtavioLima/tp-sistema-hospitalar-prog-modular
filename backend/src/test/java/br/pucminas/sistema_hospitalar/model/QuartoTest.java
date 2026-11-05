package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuartoTest {

    @Test
    void quartoNovoIniciaVazioEDisponivel() {
        Quarto quarto = new Quarto(101, 1, 2, TipoQuarto.ENFERMARIA);

        assertEquals(0, quarto.getOcupacaoAtual());
        assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
        assertTrue(quarto.verificarDisponibilidade());
    }

    @Test
    void capacidadeMenorQueUmEhRejeitada() {
        assertThrows(RegraNegocioException.class, () -> new Quarto(101, 1, 0, TipoQuarto.UTI));
    }

    @Test
    void situacaoMudaParaOcupadoSomenteAoAtingirACapacidade() {
        Quarto quarto = new Quarto(101, 1, 2, TipoQuarto.ENFERMARIA);

        quarto.adicionarPaciente();
        assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());

        quarto.adicionarPaciente();
        assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());
        assertFalse(quarto.verificarDisponibilidade());
    }

    @Test
    void ocupacaoNuncaPassaDaCapacidade() {
        Quarto quarto = new Quarto(101, 1, 1, TipoQuarto.APARTAMENTO);
        quarto.adicionarPaciente();

        assertThrows(RegraNegocioException.class, quarto::adicionarPaciente);
        assertEquals(1, quarto.getOcupacaoAtual());
    }

    @Test
    void removerPacienteLiberaOQuarto() {
        Quarto quarto = new Quarto(101, 1, 1, TipoQuarto.APARTAMENTO);
        quarto.adicionarPaciente();

        quarto.removerPaciente();

        assertEquals(0, quarto.getOcupacaoAtual());
        assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
    }

    @Test
    void naoRemovePacienteDeQuartoVazio() {
        Quarto quarto = new Quarto(101, 1, 1, TipoQuarto.APARTAMENTO);

        assertThrows(RegraNegocioException.class, quarto::removerPaciente);
        assertEquals(0, quarto.getOcupacaoAtual());
    }

    @Test
    void capacidadeNaoPodeFicarMenorQueOcupacao() {
        Quarto quarto = new Quarto(101, 1, 2, TipoQuarto.ENFERMARIA);
        quarto.adicionarPaciente();
        quarto.adicionarPaciente();

        assertThrows(RegraNegocioException.class,
                () -> quarto.atualizarDados(101, 1, 1, TipoQuarto.ENFERMARIA));
        assertEquals(2, quarto.getCapacidadeMaxima());
    }

    @Test
    void aumentarCapacidadeDeQuartoLotadoODeixaDisponivel() {
        Quarto quarto = new Quarto(101, 1, 1, TipoQuarto.ENFERMARIA);
        quarto.adicionarPaciente();

        quarto.atualizarDados(101, 1, 2, TipoQuarto.ENFERMARIA);

        assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
    }
}
