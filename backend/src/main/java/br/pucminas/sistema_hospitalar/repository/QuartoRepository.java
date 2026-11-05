package br.pucminas.sistema_hospitalar.repository;

import br.pucminas.sistema_hospitalar.model.Quarto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class QuartoRepository {

    private final List<Quarto> quartos = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    public Quarto salvar(Quarto quarto) {
        if (quarto.getId() == null) {
            quarto.setId(contadorId.getAndIncrement());
        } else {
            remover(quarto.getId());
        }
        quartos.add(quarto);
        return quarto;
    }

    public List<Quarto> listarTodos() {
        return new ArrayList<>(quartos);
    }

    public Optional<Quarto> buscarPorId(Long id) {
        return quartos.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst();
    }

    public boolean remover(Long id) {
        return quartos.removeIf(q -> q.getId().equals(id));
    }
}
