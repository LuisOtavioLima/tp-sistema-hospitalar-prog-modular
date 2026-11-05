package br.pucminas.sistema_hospitalar.model;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Atendimento {
    
    private Long id;
    private LocalDate data;
    private LocalTime horario;
    private Paciente paciente; // Vai dar um pequeno erro até criarmos a classe Paciente
    private ProfissionalSaude profissional;
    private String motivo;

    // Construtor vazio
    public Atendimento() {
    }

    // Construtor com atributos
    public Atendimento(Long id, LocalDate data, LocalTime horario, Paciente paciente, ProfissionalSaude profissional, String motivo) {
        this.id = id;
        this.data = data;
        this.horario = horario;
        this.paciente = paciente;
        this.profissional = profissional;
        this.motivo = motivo;
    }

    // O método abstrato que cada modalidade filha (Consulta, Emergência, etc.) terá que implementar com as suas próprias regras
    public abstract void validarCondicoes();

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public ProfissionalSaude getProfissional() { return profissional; }
    public void setProfissional(ProfissionalSaude profissional) { this.profissional = profissional; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}