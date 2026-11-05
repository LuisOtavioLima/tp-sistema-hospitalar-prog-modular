package br.pucminas.sistema_hospitalar.repository;

import br.pucminas.sistema_hospitalar.model.AtendimentoEmergencia;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AtendimentoEmergenciaRepository {

    private final List<AtendimentoEmergencia> atendimentos = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    public AtendimentoEmergencia salvar(AtendimentoEmergencia atendimento) {
        if (atendimento.getId() == null) {
            atendimento.setId(contadorId.getAndIncrement());
        } else {
            remover(atendimento.getId());
        }
        atendimentos.add(atendimento);
        return atendimento;
    }

    public List<AtendimentoEmergencia> listarTodos() {
        return new ArrayList<>(atendimentos);
    }

    public Optional<AtendimentoEmergencia> buscarPorId(Long id) {
        return atendimentos.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    public boolean remover(Long id) {
        return atendimentos.removeIf(a -> a.getId().equals(id));
    }
}
