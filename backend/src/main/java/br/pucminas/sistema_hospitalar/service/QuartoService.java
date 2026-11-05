package br.pucminas.sistema_hospitalar.service;

import br.pucminas.sistema_hospitalar.dto.QuartoRequest;
import br.pucminas.sistema_hospitalar.exception.RecursoNaoEncontradoException;
import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;
import br.pucminas.sistema_hospitalar.model.Quarto;
import br.pucminas.sistema_hospitalar.repository.QuartoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository repository;

    public QuartoService(QuartoRepository repository) {
        this.repository = repository;
    }

    public Quarto criar(QuartoRequest dados) {
        validarNumeroUnico(dados.numeroIdentificacao(), null);
        Quarto quarto = new Quarto(dados.numeroIdentificacao(), dados.andar(), dados.capacidadeMaxima(), dados.tipo());
        return repository.salvar(quarto);
    }

    public List<Quarto> listarTodos() {
        return repository.listarTodos();
    }

    public List<Quarto> listarDisponiveis() {
        return repository.listarTodos().stream()
                .filter(Quarto::verificarDisponibilidade)
                .toList();
    }

    public Quarto buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quarto não encontrado"));
    }

    public Quarto atualizar(Long id, QuartoRequest dados) {
        Quarto quarto = buscarPorId(id);
        validarNumeroUnico(dados.numeroIdentificacao(), id);
        quarto.atualizarDados(dados.numeroIdentificacao(), dados.andar(), dados.capacidadeMaxima(), dados.tipo());
        return repository.salvar(quarto);
    }

    public void remover(Long id) {
        Quarto quarto = buscarPorId(id);
        if (quarto.getOcupacaoAtual() > 0) {
            throw new RegraNegocioException("Não é possível remover um quarto com pacientes internados");
        }
        repository.remover(id);
    }

    private void validarNumeroUnico(int numero, Long idAtual) {
        boolean repetido = repository.listarTodos().stream()
                .anyMatch(q -> q.getNumeroIdentificacao() == numero && !q.getId().equals(idAtual));
        if (repetido) {
            throw new RegraNegocioException("Já existe um quarto com o número " + numero);
        }
    }
}
