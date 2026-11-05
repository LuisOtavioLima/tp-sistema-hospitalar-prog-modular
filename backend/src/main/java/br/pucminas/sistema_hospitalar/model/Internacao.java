package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;

import java.time.LocalDate;

public class Internacao {

    private Long id;
    private LocalDate dataEntrada;
    private LocalDate dataPrevistaAlta;
    private LocalDate dataEfetivaAlta;
    private String observacoes;
    private String observacoesAlta;
    private Paciente paciente;
    private ProfissionalSaude profissional;
    private Quarto quarto;
    private boolean entradaRegistrada;

    public Internacao(Paciente paciente, ProfissionalSaude profissional, Quarto quarto,
                      LocalDate dataEntrada, LocalDate dataPrevistaAlta, String observacoes) {
        this.paciente = paciente;
        this.profissional = profissional;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        atualizar(dataPrevistaAlta, observacoes);
    }

    public void atualizar(LocalDate dataPrevistaAlta, String observacoes) {
        if (dataPrevistaAlta.isBefore(dataEntrada)) {
            throw new RegraNegocioException("A data prevista de alta não pode ser anterior à data de entrada");
        }
        this.dataPrevistaAlta = dataPrevistaAlta;
        this.observacoes = observacoes;
    }

    public void registrarEntrada() {
        if (entradaRegistrada) {
            throw new RegraNegocioException("A entrada desta internação já foi registrada");
        }
        quarto.adicionarPaciente();
        entradaRegistrada = true;
    }

    public void registrarAlta(LocalDate data, String observacoesAlta) {
        if (!entradaRegistrada) {
            throw new RegraNegocioException("A alta exige que a entrada tenha sido registrada");
        }
        if (dataEfetivaAlta != null) {
            throw new RegraNegocioException("A alta desta internação já foi registrada");
        }
        if (data.isBefore(dataEntrada)) {
            throw new RegraNegocioException("A data de alta não pode ser anterior à data de entrada");
        }
        quarto.removerPaciente();
        this.dataEfetivaAlta = data;
        this.observacoesAlta = observacoesAlta;
    }

    public boolean isAtiva() {
        return entradaRegistrada && dataEfetivaAlta == null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataEntrada() { return dataEntrada; }

    public LocalDate getDataPrevistaAlta() { return dataPrevistaAlta; }

    public LocalDate getDataEfetivaAlta() { return dataEfetivaAlta; }

    public String getObservacoes() { return observacoes; }

    public String getObservacoesAlta() { return observacoesAlta; }

    public Paciente getPaciente() { return paciente; }

    public ProfissionalSaude getProfissional() { return profissional; }

    public Quarto getQuarto() { return quarto; }
}
