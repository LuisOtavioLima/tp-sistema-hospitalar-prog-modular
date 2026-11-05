package br.pucminas.sistema_hospitalar.model;

import java.util.ArrayList;
import java.util.List;

public class MedicoClinicoGeral extends ProfissionalSaude {
    private List<String> consultasRealizadas = new ArrayList<>();

    public MedicoClinicoGeral() {
        super();
    }

    public void registrarConsulta(String consulta) {
        this.consultasRealizadas.add(consulta);
    }

    public void encaminharParaEspecialista(Object paciente, String especialidade) {
        // Lógica de encaminhamento para atendimento especializado
    }

    public List<String> getConsultasRealizadas() {
        return consultasRealizadas;
    }
}
