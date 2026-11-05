package br.pucminas.sistema_hospitalar.model;

public enum TipoConsulta {
    ROTINA("Consulta de rotina"),
    ESPECIALIZADA("Consulta especializada");

    private final String descricao;

    TipoConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
