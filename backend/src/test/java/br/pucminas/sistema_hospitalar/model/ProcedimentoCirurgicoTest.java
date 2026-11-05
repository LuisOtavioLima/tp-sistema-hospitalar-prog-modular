package br.pucminas.sistema_hospitalar.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProcedimentoCirurgicoTest {

    @Test
    public void testIniciarProcedimento() {
        ProcedimentoCirurgico proc = new ProcedimentoCirurgico();
        assertEquals(SituacaoProcedimento.AGENDADO, proc.getSituacao());
        
        proc.iniciarProcedimento();
        assertEquals(SituacaoProcedimento.EM_ANDAMENTO, proc.getSituacao());
    }

    @Test
    public void testConcluirProcedimentoComSucesso() {
        ProcedimentoCirurgico proc = new ProcedimentoCirurgico();
        proc.iniciarProcedimento();
        proc.concluirProcedimento();
        
        assertEquals(SituacaoProcedimento.CONCLUIDO, proc.getSituacao());
        assertNotNull(proc.getDataHoraEfetiva());
    }

    @Test
    public void testConcluirProcedimentoSemIniciarLancaException() {
        ProcedimentoCirurgico proc = new ProcedimentoCirurgico();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            proc.concluirProcedimento();
        });
        
        assertEquals("O procedimento só pode ser concluído se estiver em andamento.", exception.getMessage());
    }

    @Test
    public void testCancelarProcedimento() {
        ProcedimentoCirurgico proc = new ProcedimentoCirurgico();
        proc.cancelarProcedimento();
        
        assertEquals(SituacaoProcedimento.CANCELADO, proc.getSituacao());
    }
    
    @Test
    public void testPolimorfismoHierarquia() {
        Cirurgiao cirurgiao = new Cirurgiao();
        MedicoClinicoGeral clinico = new MedicoClinicoGeral();
        
        assertTrue(cirurgiao instanceof ProfissionalSaude);
        assertTrue(clinico instanceof ProfissionalSaude);
    }
}
