package br.pucminas.sistema_hospitalar.model;

public abstract class ProfissionalSaude {
    
    private Long id;
    private String nome;
    private String registroProfissional;
    private String telefone;
    private String email;

    public ProfissionalSaude() {
    }

    public ProfissionalSaude(Long id, String nome, String registroProfissional, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.registroProfissional = registroProfissional;
        this.telefone = telefone;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}