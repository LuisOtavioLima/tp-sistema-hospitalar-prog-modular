package br.pucminas.sistema_hospitalar.model;

import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;

public class Quarto {

    private Long id;
    private int numeroIdentificacao;
    private int andar;
    private int capacidadeMaxima;
    private int ocupacaoAtual;
    private TipoQuarto tipo;
    private SituacaoQuarto situacao;

    public Quarto(int numeroIdentificacao, int andar, int capacidadeMaxima, TipoQuarto tipo) {
        atualizarDados(numeroIdentificacao, andar, capacidadeMaxima, tipo);
    }

    public void atualizarDados(int numeroIdentificacao, int andar, int capacidadeMaxima, TipoQuarto tipo) {
        if (capacidadeMaxima < 1) {
            throw new RegraNegocioException("A capacidade máxima deve ser de pelo menos 1 paciente");
        }
        if (capacidadeMaxima < ocupacaoAtual) {
            throw new RegraNegocioException("A capacidade máxima não pode ser menor que a ocupação atual do quarto");
        }
        this.numeroIdentificacao = numeroIdentificacao;
        this.andar = andar;
        this.capacidadeMaxima = capacidadeMaxima;
        this.tipo = tipo;
        atualizarSituacao();
    }

    public boolean verificarDisponibilidade() {
        return ocupacaoAtual < capacidadeMaxima;
    }

    public void adicionarPaciente() {
        if (!verificarDisponibilidade()) {
            throw new RegraNegocioException("O quarto " + numeroIdentificacao + " atingiu a capacidade máxima");
        }
        ocupacaoAtual++;
        atualizarSituacao();
    }

    public void removerPaciente() {
        if (ocupacaoAtual == 0) {
            throw new RegraNegocioException("O quarto " + numeroIdentificacao + " não possui pacientes");
        }
        ocupacaoAtual--;
        atualizarSituacao();
    }

    private void atualizarSituacao() {
        situacao = verificarDisponibilidade() ? SituacaoQuarto.DISPONIVEL : SituacaoQuarto.OCUPADO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNumeroIdentificacao() { return numeroIdentificacao; }

    public int getAndar() { return andar; }

    public int getCapacidadeMaxima() { return capacidadeMaxima; }

    public int getOcupacaoAtual() { return ocupacaoAtual; }

    public TipoQuarto getTipo() { return tipo; }

    public SituacaoQuarto getSituacao() { return situacao; }
}
