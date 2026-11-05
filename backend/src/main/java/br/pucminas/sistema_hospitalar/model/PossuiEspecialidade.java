package br.pucminas.sistema_hospitalar.model;

// O MedicoEspecialista implementa isso; a ConsultaEspecializada só depende deste contrato
public interface PossuiEspecialidade {

    String getEspecialidade();
}
