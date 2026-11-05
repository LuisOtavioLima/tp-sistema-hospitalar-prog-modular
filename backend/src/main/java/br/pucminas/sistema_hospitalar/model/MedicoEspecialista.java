package br.pucminas.sistema_hospitalar.model;

public class MedicoEspecialista extends ProfissionalSaude implements PossuiEspecialidade {
    private String especialidade;

    public MedicoEspecialista() {
        super();
    }

    @Override
    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}