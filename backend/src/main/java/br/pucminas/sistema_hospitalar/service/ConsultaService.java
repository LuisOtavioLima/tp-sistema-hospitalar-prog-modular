package br.pucminas.sistema_hospitalar.service;

import br.pucminas.sistema_hospitalar.dto.ConsultaRequest;
import br.pucminas.sistema_hospitalar.dto.ConsultaUpdateRequest;
import br.pucminas.sistema_hospitalar.dto.ReagendamentoRequest;
import br.pucminas.sistema_hospitalar.exception.RecursoNaoEncontradoException;
import br.pucminas.sistema_hospitalar.exception.RegraNegocioException;
import br.pucminas.sistema_hospitalar.model.Consulta;
import br.pucminas.sistema_hospitalar.model.ConsultaEspecializada;
import br.pucminas.sistema_hospitalar.model.ConsultaRotina;
import br.pucminas.sistema_hospitalar.model.Paciente;
import br.pucminas.sistema_hospitalar.model.ProfissionalSaude;
import br.pucminas.sistema_hospitalar.model.SituacaoConsulta;
import br.pucminas.sistema_hospitalar.repository.ConsultaRepository;
import br.pucminas.sistema_hospitalar.repository.PacienteRepository;
import br.pucminas.sistema_hospitalar.repository.ProfissionalSaudeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ConsultaService {

    private final ConsultaRepository repository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalSaudeRepository profissionalRepository;

    public ConsultaService(ConsultaRepository repository, PacienteRepository pacienteRepository,
                           ProfissionalSaudeRepository profissionalRepository) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    public Consulta agendar(ConsultaRequest dados) {
        Paciente paciente = pacienteRepository.buscarPorId(dados.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
        ProfissionalSaude profissional = buscarProfissional(dados.profissionalId());

        Consulta consulta = switch (dados.tipo()) {
            case ROTINA -> new ConsultaRotina(dados.data(), dados.horario(), paciente, profissional, dados.motivo());
            case ESPECIALIZADA -> new ConsultaEspecializada(dados.data(), dados.horario(), paciente, profissional,
                    dados.motivo(), dados.especialidade());
        };
        consulta.validarCondicoes();
        validarHorarioDisponivel(profissional, dados.data(), dados.horario(), null);
        return repository.salvar(consulta);
    }

    public List<Consulta> listar(SituacaoConsulta situacao, Long pacienteId, Long profissionalId) {
        return repository.listarTodas().stream()
                .filter(c -> situacao == null || c.getSituacao() == situacao)
                .filter(c -> pacienteId == null || c.getPaciente().getId().equals(pacienteId))
                .filter(c -> profissionalId == null || c.getProfissional().getId().equals(profissionalId))
                .sorted(Comparator.comparing(Consulta::getData).thenComparing(Consulta::getHorario))
                .toList();
    }

    public Consulta buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada"));
    }

    public Consulta atualizar(Long id, ConsultaUpdateRequest dados) {
        Consulta consulta = buscarPorId(id);
        ProfissionalSaude profissional = buscarProfissional(dados.profissionalId());
        validarHorarioDisponivel(profissional, dados.data(), dados.horario(), id);
        consulta.atualizarAgendamento(profissional, dados.data(), dados.horario(), dados.motivo());
        return repository.salvar(consulta);
    }

    public Consulta confirmar(Long id) {
        Consulta consulta = buscarPorId(id);
        consulta.confirmar();
        return repository.salvar(consulta);
    }

    public Consulta iniciar(Long id) {
        Consulta consulta = buscarPorId(id);
        consulta.iniciar();
        return repository.salvar(consulta);
    }

    public Consulta concluir(Long id, String observacoes) {
        Consulta consulta = buscarPorId(id);
        consulta.concluir(observacoes);
        pacienteRepository.buscarPorId(consulta.getPaciente().getId())
                .ifPresent(paciente -> paciente.adicionarNoHistorico(consulta.resumo()));
        return repository.salvar(consulta);
    }

    public Consulta cancelar(Long id) {
        Consulta consulta = buscarPorId(id);
        consulta.cancelar();
        return repository.salvar(consulta);
    }

    public Consulta registrarNaoComparecimento(Long id) {
        Consulta consulta = buscarPorId(id);
        consulta.registrarNaoComparecimento();
        return repository.salvar(consulta);
    }

    public Consulta reagendar(Long id, ReagendamentoRequest dados) {
        Consulta consulta = buscarPorId(id);
        validarHorarioDisponivel(consulta.getProfissional(), dados.data(), dados.horario(), id);
        consulta.reagendar(dados.data(), dados.horario());
        return repository.salvar(consulta);
    }

    public Consulta registrarObservacoes(Long id, String observacoes) {
        Consulta consulta = buscarPorId(id);
        consulta.registrarObservacoes(observacoes);
        return repository.salvar(consulta);
    }

    public void remover(Long id) {
        Consulta consulta = buscarPorId(id);
        // o que já foi atendido faz parte do histórico do paciente
        if (consulta.getSituacao() == SituacaoConsulta.EM_ATENDIMENTO
                || consulta.getSituacao() == SituacaoConsulta.CONCLUIDA) {
            throw new RegraNegocioException("Não é possível remover uma consulta em atendimento ou concluída");
        }
        repository.remover(id);
    }

    private ProfissionalSaude buscarProfissional(Long id) {
        return profissionalRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado"));
    }

    private void validarHorarioDisponivel(ProfissionalSaude profissional, LocalDate data, LocalTime horario,
                                          Long consultaIgnorada) {
        if (LocalDateTime.of(data, horario).isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível agendar uma consulta para um horário que já passou");
        }
        boolean ocupado = repository.listarTodas().stream()
                .filter(c -> !Objects.equals(c.getId(), consultaIgnorada))
                .filter(Consulta::ocupaAgenda)
                .anyMatch(c -> c.getProfissional().getId().equals(profissional.getId())
                        && c.getData().equals(data)
                        && c.getHorario().equals(horario));
        if (ocupado) {
            throw new RegraNegocioException("O profissional já possui um atendimento agendado para esse horário");
        }
    }
}
