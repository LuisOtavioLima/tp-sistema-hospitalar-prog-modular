package br.pucminas.sistema_hospitalar.service;

import br.pucminas.sistema_hospitalar.dto.AltaRequest;
import br.pucminas.sistema_hospitalar.dto.InternacaoRequest;
import br.pucminas.sistema_hospitalar.dto.InternacaoUpdateRequest;
import br.pucminas.sistema_hospitalar.exception.RecursoNaoEncontradoException;
import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;
import br.pucminas.sistema_hospitalar.model.Internacao;
import br.pucminas.sistema_hospitalar.model.Paciente;
import br.pucminas.sistema_hospitalar.model.ProfissionalSaude;
import br.pucminas.sistema_hospitalar.model.Quarto;
import br.pucminas.sistema_hospitalar.repository.InternacaoRepository;
import br.pucminas.sistema_hospitalar.repository.PacienteRepository;
import br.pucminas.sistema_hospitalar.repository.ProfissionalSaudeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternacaoService {

    private final InternacaoRepository repository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;
    private final QuartoService quartoService;

    public InternacaoService(InternacaoRepository repository, PacienteRepository pacienteRepository,
                             ProfissionalSaudeRepository profissionalRepository, QuartoService quartoService) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
        this.quartoService = quartoService;
    }

    public Internacao criar(InternacaoRequest dados) {
        Paciente paciente = pacienteRepository.buscarPorId(dados.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
        ProfissionalSaude profissional = profissionalRepository.buscarPorId(dados.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado"));
        Quarto quarto = quartoService.buscarPorId(dados.quartoId());

        boolean jaInternado = repository.listarTodos().stream()
                .anyMatch(i -> i.isAtiva() && i.getPaciente().getId().equals(paciente.getId()));
        if (jaInternado) {
            throw new RegraNegocioException("O paciente já possui uma internação ativa");
        }

        Internacao internacao = new Internacao(paciente, profissional, quarto,
                dados.dataEntrada(), dados.dataPrevistaAlta(), dados.observacoes());
        internacao.registrarEntrada();
        return repository.salvar(internacao);
    }

    public List<Internacao> listarTodas() {
        return repository.listarTodos();
    }

    public Internacao buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Internação não encontrada"));
    }

    public Internacao atualizar(Long id, InternacaoUpdateRequest dados) {
        Internacao internacao = buscarPorId(id);
        internacao.atualizar(dados.dataPrevistaAlta(), dados.observacoes());
        return repository.salvar(internacao);
    }

    public Internacao registrarAlta(Long id, AltaRequest dados) {
        Internacao internacao = buscarPorId(id);
        internacao.registrarAlta(dados.data(), dados.observacoes());
        pacienteRepository.buscarPorId(internacao.getPaciente().getId())
                .ifPresent(paciente -> paciente.adicionarNoHistorico(
                        "Internação no quarto " + internacao.getQuarto().getNumeroIdentificacao()
                                + " de " + internacao.getDataEntrada() + " a " + internacao.getDataEfetivaAlta()));
        return repository.salvar(internacao);
    }

    public void remover(Long id) {
        Internacao internacao = buscarPorId(id);
        if (internacao.isAtiva()) {
            internacao.getQuarto().removerPaciente();
        }
        repository.remover(id);
    }
}
