package br.pucminas.sistema_hospitalar.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ConsultaRotina extends Consulta {

    public ConsultaRotina(LocalDate data, LocalTime horario, Paciente paciente,
                          ProfissionalSaude profissional, String motivo) {
        super(data, horario, paciente, profissional, motivo);
    }

    @Override
    public void validarCondicoes() {
        validarDadosComuns();
    }

    @Override
    public TipoConsulta getTipo() {
        return TipoConsulta.ROTINA;
    }
}
