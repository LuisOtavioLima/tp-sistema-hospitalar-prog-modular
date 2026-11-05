package br.pucminas.sistema_hospitalar.model;

// Substitui o MedicoEspecialista enquanto ele não está na main
public class EspecialistaDeTeste extends ProfissionalSaude implements PossuiEspecialidade {

    private final String especialidade;

    public EspecialistaDeTeste(Long id, String nome, String especialidade) {
        super(id, nome, "CRM-" + nome, "3199999", nome.toLowerCase() + "@email.com");
        this.especialidade = especialidade;
    }

    @Override
    public String getEspecialidade() {
        return especialidade;
    }
}
