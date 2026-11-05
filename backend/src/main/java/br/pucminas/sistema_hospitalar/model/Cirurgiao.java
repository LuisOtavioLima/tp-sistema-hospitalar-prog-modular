package br.pucminas.sistema_hospitalar.model;

import java.util.ArrayList;
import java.util.List;

public class Cirurgiao extends ProfissionalSaude {
    private List<ProcedimentoCirurgico> procedimentosRealizados = new ArrayList<>();
    private String informacoesAtuacao;

    public Cirurgiao() {
        super();
    }

    public List<ProcedimentoCirurgico> getProcedimentosRealizados() {
        return procedimentosRealizados;
    }

    public void registrarProcedimento(ProcedimentoCirurgico procedimento) {
        this.procedimentosRealizados.add(procedimento);
    }

    public String getInformacoesAtuacao() {
        return informacoesAtuacao;
    }

    public void setInformacoesAtuacao(String informacoesAtuacao) {
        this.informacoesAtuacao = informacoesAtuacao;
    }
}
