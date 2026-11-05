package br.pucminas.sistema_hospitalar.model;

import java.time.LocalDateTime;

class Atendimento {} 

public class ProcedimentoCirurgico extends Atendimento {
    private String descricao;
    private String tipoCirurgia;
    private String observacoesPreOperatorias;
    private String observacoesPosOperatorias;
    private LocalDateTime dataHoraPrevista;
    private LocalDateTime dataHoraEfetiva;
    private Paciente paciente; 
    private Cirurgiao cirurgiao;
    private SituacaoProcedimento situacao = SituacaoProcedimento.AGENDADO;

    public void iniciarProcedimento() {
        this.situacao = SituacaoProcedimento.EM_ANDAMENTO;
    }

    public void concluirProcedimento() {
        if (this.situacao != SituacaoProcedimento.EM_ANDAMENTO) {
            throw new IllegalStateException("O procedimento só pode ser concluído se estiver em andamento.");
        }
        this.situacao = SituacaoProcedimento.CONCLUIDO;
        this.dataHoraEfetiva = LocalDateTime.now();
    }

    public void cancelarProcedimento() {
        this.situacao = SituacaoProcedimento.CANCELADO;
    }

    public SituacaoProcedimento getSituacao() { return situacao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipoCirurgia() { return tipoCirurgia; }
    public void setTipoCirurgia(String tipoCirurgia) { this.tipoCirurgia = tipoCirurgia; }
    public LocalDateTime getDataHoraPrevista() { return dataHoraPrevista; }
    public void setDataHoraPrevista(LocalDateTime dataHoraPrevista) { this.dataHoraPrevista = dataHoraPrevista; }
    public LocalDateTime getDataHoraEfetiva() { return dataHoraEfetiva; }
    public Cirurgiao getCirurgiao() { return cirurgiao; }
    public void setCirurgiao(Cirurgiao cirurgiao) { this.cirurgiao = cirurgiao; }
}
