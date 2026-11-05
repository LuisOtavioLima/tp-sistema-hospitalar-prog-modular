package br.pucminas.sistema_hospitalar.repository;

import br.pucminas.sistema_hospitalar.model.Internacao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InternacaoRepository {

    private final List<Internacao> internacoes = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    public Internacao salvar(Internacao internacao) {
        if (internacao.getId() == null) {
            internacao.setId(contadorId.getAndIncrement());
        } else {
            remover(internacao.getId());
        }
        internacoes.add(internacao);
        return internacao;
    }

    public List<Internacao> listarTodos() {
        return new ArrayList<>(internacoes);
    }

    public Optional<Internacao> buscarPorId(Long id) {
        return internacoes.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
    }

    public boolean remover(Long id) {
        return internacoes.removeIf(i -> i.getId().equals(id));
    }
}
