package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;

import java.time.LocalDate;
import java.time.LocalTime;

public class AtendimentoEmergencia extends Atendimento {

    private PrioridadeAtendimento prioridade;
    private SituacaoEmergencia situacao = SituacaoEmergencia.AGUARDANDO;

    public AtendimentoEmergencia(LocalDate data, LocalTime horario, Paciente paciente, String motivo,
                                 PrioridadeAtendimento prioridade) {
        super(null, data, horario, paciente, null, motivo);
        this.prioridade = prioridade;
    }

    @Override
    public void validarCondicoes() {
        if (getPaciente() == null) {
            throw new RegraNegocioException("O atendimento de emergência exige um paciente");
        }
        if (prioridade == null) {
            throw new RegraNegocioException("O atendimento de emergência exige uma classificação de prioridade");
        }
        if (getProfissional() == null) {
            throw new RegraNegocioException("O atendimento de emergência exige um profissional responsável");
        }
    }

    public void iniciar(ProfissionalSaude profissional) {
        if (situacao != SituacaoEmergencia.AGUARDANDO) {
            throw new RegraNegocioException("Somente um atendimento aguardando avaliação pode ser iniciado");
        }
        setProfissional(profissional);
        validarCondicoes();
        situacao = SituacaoEmergencia.INICIADO;
    }

    public void concluir() {
        if (situacao != SituacaoEmergencia.INICIADO) {
            throw new RegraNegocioException("Somente um atendimento iniciado pode ser concluído");
        }
        situacao = SituacaoEmergencia.CONCLUIDO;
    }

    public PrioridadeAtendimento getPrioridade() { return prioridade; }

    public SituacaoEmergencia getSituacao() { return situacao; }
}
