package br.pucminas.sistema_hospitalar.service;

import br.pucminas.sistema_hospitalar.model.Paciente;
import br.pucminas.sistema_hospitalar.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public Paciente salvar(Paciente paciente) {
        return repository.salvar(paciente);
    }

    public List<Paciente> listarTodos() {
        return repository.listarTodos();
    }

    public Paciente buscarPorId(Long id) {
        // Lançar exceção genérica por enquanto. Trataremos isso no Global Exception Handler!
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    public Paciente atualizar(Long id, Paciente pacienteAtualizado) {
        Paciente pacienteExistente = buscarPorId(id);
        pacienteAtualizado.setId(pacienteExistente.getId());
        return repository.salvar(pacienteAtualizado);
    }

    public void remover(Long id) {
        if (!repository.remover(id)) {
            throw new RuntimeException("Paciente não encontrado");
        }
    }
}
