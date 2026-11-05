package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaEspecializadaTest {

    private final Paciente paciente = new Paciente(1L, "Maria", "52998224725", "maria@email.com", null, null);
    private final EspecialistaDeTeste cardiologista = new EspecialistaDeTeste(1L, "Bruno", "Cardiologia");
    private final EspecialistaDeTeste ortopedista = new EspecialistaDeTeste(2L, "Carla", "Ortopedia");
    private final ProfissionalSaude clinico = new ProfissionalSaude(3L, "Ana", "CRM-3", "3199999", "ana@email.com") {
    };
    private final LocalDate data = LocalDate.of(2026, 12, 10);
    private final LocalTime horario = LocalTime.of(14, 0);

    private ConsultaEspecializada consultaCom(ProfissionalSaude profissional, String especialidade) {
        return new ConsultaEspecializada(data, horario, paciente, profissional, "Dor no peito", especialidade);
    }

    @Test
    void aceitaProfissionalDaMesmaEspecialidade() {
        ConsultaEspecializada consulta = consultaCom(cardiologista, "cardiologia");
        assertDoesNotThrow(consulta::validarCondicoes);
        assertEquals(TipoConsulta.ESPECIALIZADA, consulta.getTipo());
    }

    @Test
    void recusaEspecialidadeDiferente() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> consultaCom(ortopedista, "Cardiologia").validarCondicoes());
        assertTrue(erro.getMessage().contains("Cardiologia"));
    }

    @Test
    void recusaProfissionalSemEspecialidade() {
        assertThrows(RegraNegocioException.class, () -> consultaCom(clinico, "Cardiologia").validarCondicoes());
    }

    @Test
    void exigeEspecialidade() {
        assertThrows(RegraNegocioException.class, () -> consultaCom(cardiologista, null).validarCondicoes());
        assertThrows(RegraNegocioException.class, () -> consultaCom(cardiologista, " ").validarCondicoes());
    }

    @Test
    void naoTrocaParaProfissionalIncompativel() {
        ConsultaEspecializada consulta = consultaCom(cardiologista, "Cardiologia");

        assertThrows(RegraNegocioException.class,
                () -> consulta.atualizarAgendamento(ortopedista, data.plusDays(1), horario, "Retorno"));

        assertSame(cardiologista, consulta.getProfissional());
        assertEquals(data, consulta.getData());
    }

    @Test
    void resumoIncluiAEspecialidade() {
        ConsultaEspecializada consulta = consultaCom(cardiologista, "Cardiologia");
        assertTrue(consulta.resumo().startsWith("Consulta especializada (Cardiologia)"));
    }
}
