package br.pucminas.sistema_hospitalar.service;

import br.pucminas.sistema_hospitalar.dto.AtendimentoEmergenciaRequest;
import br.pucminas.sistema_hospitalar.exception.RecursoNaoEncontradoException;
import br.pucminas.sistema_hospitalar.model.AtendimentoEmergencia;
import br.pucminas.sistema_hospitalar.model.Paciente;
import br.pucminas.sistema_hospitalar.model.ProfissionalSaude;
import br.pucminas.sistema_hospitalar.model.SituacaoEmergencia;
import br.pucminas.sistema_hospitalar.repository.AtendimentoEmergenciaRepository;
import br.pucminas.sistema_hospitalar.repository.PacienteRepository;
import br.pucminas.sistema_hospitalar.repository.ProfissionalSaudeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AtendimentoEmergenciaService {

    private final AtendimentoEmergenciaRepository repository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public AtendimentoEmergenciaService(AtendimentoEmergenciaRepository repository,
                                        PacienteRepository pacienteRepository,
                                        ProfissionalSaudeRepository profissionalRepository) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    public AtendimentoEmergencia registrar(AtendimentoEmergenciaRequest dados) {
        Paciente paciente = pacienteRepository.buscarPorId(dados.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
        AtendimentoEmergencia atendimento = new AtendimentoEmergencia(LocalDate.now(), LocalTime.now(),
                paciente, dados.motivo(), dados.prioridade());
        return repository.salvar(atendimento);
    }

    public List<AtendimentoEmergencia> listarPorSituacao(SituacaoEmergencia situacao) {
        return repository.listarTodos().stream()
                .filter(a -> a.getSituacao() == situacao)
                .sorted(Comparator.comparing(AtendimentoEmergencia::getPrioridade, Comparator.reverseOrder())
                        .thenComparing(AtendimentoEmergencia::getData)
                        .thenComparing(AtendimentoEmergencia::getHorario))
                .toList();
    }

    public AtendimentoEmergencia buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento de emergência não encontrado"));
    }

    public AtendimentoEmergencia iniciar(Long id, Long profissionalId) {
        AtendimentoEmergencia atendimento = buscarPorId(id);
        ProfissionalSaude profissional = profissionalRepository.buscarPorId(profissionalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado"));
        atendimento.iniciar(profissional);
        return repository.salvar(atendimento);
    }

    public AtendimentoEmergencia concluir(Long id) {
        AtendimentoEmergencia atendimento = buscarPorId(id);
        atendimento.concluir();
        return repository.salvar(atendimento);
    }
}
