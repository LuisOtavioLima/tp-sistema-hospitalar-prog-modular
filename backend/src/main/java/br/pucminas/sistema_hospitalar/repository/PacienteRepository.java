package br.pucminas.sistema_hospitalar.repository;

import br.pucminas.sistema_hospitalar.model.Paciente;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PacienteRepository {
    
    private final List<Paciente> pacientes = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    public Paciente salvar(Paciente paciente) {
        if (paciente.getId() == null) {
            paciente.setId(contadorId.getAndIncrement());
            pacientes.add(paciente);
        } else {
            // att: remove o antigo e insere o novo
            remover(paciente.getId());
            pacientes.add(paciente);
        }
        return paciente;
    }

    public List<Paciente> listarTodos() {
        return new ArrayList<>(pacientes);
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return pacientes.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public boolean remover(Long id) {
        return pacientes.removeIf(p -> p.getId().equals(id));
    }
}