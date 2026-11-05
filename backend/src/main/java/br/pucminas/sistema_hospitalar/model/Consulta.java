package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Consulta extends Atendimento {

    private SituacaoConsulta situacao = SituacaoConsulta.AGENDADA;
    private String observacoes;

    protected Consulta(LocalDate data, LocalTime horario, Paciente paciente,
                       ProfissionalSaude profissional, String motivo) {
        super(null, data, horario, paciente, profissional, motivo);
    }

    public abstract TipoConsulta getTipo();

    public void confirmar() {
        mudarSituacao(SituacaoConsulta.CONFIRMADA);
    }

    public void iniciar() {
        validarCondicoes();
        mudarSituacao(SituacaoConsulta.EM_ATENDIMENTO);
    }

    public void concluir(String observacoes) {
        mudarSituacao(SituacaoConsulta.CONCLUIDA);
        if (observacoes != null && !observacoes.isBlank()) {
            this.observacoes = observacoes;
        }
    }

    public void cancelar() {
        mudarSituacao(SituacaoConsulta.CANCELADA);
    }

    public void registrarNaoComparecimento() {
        mudarSituacao(SituacaoConsulta.NAO_COMPARECEU);
    }

    public void reagendar(LocalDate data, LocalTime horario) {
        mudarSituacao(SituacaoConsulta.AGENDADA);
        setData(data);
        setHorario(horario);
    }

    public void atualizarAgendamento(ProfissionalSaude profissional, LocalDate data, LocalTime horario, String motivo) {
        if (!isEditavel()) {
            throw new RegraNegocioException("Só é possível alterar uma consulta agendada ou confirmada");
        }
        // valida antes de mexer, pra não deixar a consulta pela metade
        validarAgendamento(profissional, data, horario, motivo);
        setProfissional(profissional);
        setData(data);
        setHorario(horario);
        setMotivo(motivo);
    }

    public void registrarObservacoes(String observacoes) {
        if (situacao != SituacaoConsulta.EM_ATENDIMENTO && situacao != SituacaoConsulta.CONCLUIDA) {
            throw new RegraNegocioException("As observações só podem ser registradas durante ou após o atendimento");
        }
        if (observacoes == null || observacoes.isBlank()) {
            throw new RegraNegocioException("As observações não podem ficar em branco");
        }
        this.observacoes = observacoes;
    }

    public boolean isEditavel() {
        return situacao == SituacaoConsulta.AGENDADA || situacao == SituacaoConsulta.CONFIRMADA;
    }

    // cancelada ou falta liberam o horário do profissional
    public boolean ocupaAgenda() {
        return situacao != SituacaoConsulta.CANCELADA && situacao != SituacaoConsulta.NAO_COMPARECEU;
    }

    public String resumo() {
        String texto = descricao() + " em " + getData() + " às " + getHorario()
                + " com " + getProfissional().getNome() + ": " + getMotivo();
        return observacoes == null ? texto : texto + ". Observações: " + observacoes;
    }

    protected String descricao() {
        return getTipo().getDescricao();
    }

    protected void validarDadosComuns() {
        if (getPaciente() == null) {
            throw new RegraNegocioException("A consulta exige um paciente");
        }
        validarAgendamento(getProfissional(), getData(), getHorario(), getMotivo());
    }

    protected void validarProfissional(ProfissionalSaude profissional) {
        if (profissional == null) {
            throw new RegraNegocioException("A consulta exige um profissional responsável");
        }
    }

    private void validarAgendamento(ProfissionalSaude profissional, LocalDate data, LocalTime horario, String motivo) {
        if (data == null || horario == null) {
            throw new RegraNegocioException("A consulta exige data e horário");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new RegraNegocioException("A consulta exige um motivo");
        }
        validarProfissional(profissional);
    }

    private void mudarSituacao(SituacaoConsulta nova) {
        if (!situacao.podeMudarPara(nova)) {
            throw new RegraNegocioException("Não é possível passar a consulta de " + situacao + " para " + nova);
        }
        situacao = nova;
    }

    public SituacaoConsulta getSituacao() { return situacao; }

    public String getObservacoes() { return observacoes; }
}
