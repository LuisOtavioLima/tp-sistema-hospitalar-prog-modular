package br.pucminas.sistema_hospitalar.model;

public class MedicoEspecialista extends ProfissionalSaude {
    private String especialidade;

    public MedicoEspecialista() {
        super();
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
