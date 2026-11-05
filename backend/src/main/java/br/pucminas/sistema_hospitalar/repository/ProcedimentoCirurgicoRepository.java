package br.pucminas.sistema_hospitalar.repository;

import br.pucminas.sistema_hospitalar.model.ProcedimentoCirurgico;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
//import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProcedimentoCirurgicoRepository {

    private final List<ProcedimentoCirurgico> procedimentos = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    public ProcedimentoCirurgico salvar(ProcedimentoCirurgico procedimento) {
        if (procedimento.getId() == null) {
            procedimento.setId(contadorId.getAndIncrement());
        } else {
            remover(procedimento.getId());
        }
        procedimentos.add(procedimento);
        return procedimento;
    }

    public List<ProcedimentoCirurgico> listarTodos() {
        return new ArrayList<>(procedimentos);
    }

    public boolean remover(Long id) {
        return procedimentos.removeIf(p -> p.getId().equals(id));
    }

    // NOVO MÉTODO PARA BUSCAR POR ID DO CIRURGIÃO
    public List<ProcedimentoCirurgico> buscarPorCirurgiaoId(Long id) {
        return procedimentos.stream()
                .filter(p -> p.getCirurgiao() != null && 
                             p.getCirurgiao().getId() != null &&
                             p.getCirurgiao().getId().equals(id))
                .collect(Collectors.toList());
    }
}