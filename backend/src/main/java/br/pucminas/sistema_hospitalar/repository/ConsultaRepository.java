package br.pucminas.sistema_hospitalar.repository;

import br.pucminas.sistema_hospitalar.model.Consulta;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ConsultaRepository {

    private final List<Consulta> consultas = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    public Consulta salvar(Consulta consulta) {
        if (consulta.getId() == null) {
            consulta.setId(contadorId.getAndIncrement());
        } else {
            remover(consulta.getId());
        }
        consultas.add(consulta);
        return consulta;
    }

    public List<Consulta> listarTodas() {
        return new ArrayList<>(consultas);
    }

    public Optional<Consulta> buscarPorId(Long id) {
        return consultas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public boolean remover(Long id) {
        return consultas.removeIf(c -> c.getId().equals(id));
    }
}
