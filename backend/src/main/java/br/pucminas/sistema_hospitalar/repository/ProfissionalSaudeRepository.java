package br.pucminas.sistema_hospitalar.repository;

import br.pucminas.sistema_hospitalar.model.ProfissionalSaude;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProfissionalSaudeRepository {

    private final List<ProfissionalSaude> profissionais = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    public ProfissionalSaude salvar(ProfissionalSaude profissional) {
        if (profissional.getId() == null) {
            profissional.setId(contadorId.getAndIncrement());
        } else {
            remover(profissional.getId());
        }
        profissionais.add(profissional);
        return profissional;
    }

    public List<ProfissionalSaude> listarTodos() {
        return new ArrayList<>(profissionais);
    }

    public Optional<ProfissionalSaude> buscarPorId(Long id) {
        return profissionais.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public boolean remover(Long id) {
        return profissionais.removeIf(p -> p.getId().equals(id));
    }
}
