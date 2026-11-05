package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;

import java.time.LocalDate;
import java.time.LocalTime;

public class ConsultaEspecializada extends Consulta {

    private final String especialidade;

    public ConsultaEspecializada(LocalDate data, LocalTime horario, Paciente paciente,
                                 ProfissionalSaude profissional, String motivo, String especialidade) {
        super(data, horario, paciente, profissional, motivo);
        this.especialidade = especialidade;
    }

    @Override
    public void validarCondicoes() {
        if (especialidade == null || especialidade.isBlank()) {
            throw new RegraNegocioException("A consulta especializada exige uma especialidade");
        }
        validarDadosComuns();
    }

    @Override
    protected void validarProfissional(ProfissionalSaude profissional) {
        super.validarProfissional(profissional);
        boolean compativel = profissional instanceof PossuiEspecialidade especialista
                && especialidade.equalsIgnoreCase(especialista.getEspecialidade());
        if (!compativel) {
            throw new RegraNegocioException("O profissional " + profissional.getNome()
                    + " não atende a especialidade " + especialidade);
        }
    }

    @Override
    protected String descricao() {
        return super.descricao() + " (" + especialidade + ")";
    }

    @Override
    public TipoConsulta getTipo() {
        return TipoConsulta.ESPECIALIZADA;
    }

    public String getEspecialidade() { return especialidade; }
}
