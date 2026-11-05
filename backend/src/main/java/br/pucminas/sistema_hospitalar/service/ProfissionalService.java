package br.pucminas.sistema_hospitalar.service;

import br.pucminas.sistema_hospitalar.model.ProcedimentoCirurgico;
import br.pucminas.sistema_hospitalar.model.ProfissionalSaude;
import br.pucminas.sistema_hospitalar.repository.ProcedimentoCirurgicoRepository;
import br.pucminas.sistema_hospitalar.repository.ProfissionalSaudeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfissionalService {

    private final ProfissionalSaudeRepository profissionalRepository;
    private final ProcedimentoCirurgicoRepository procedimentoRepository;

    public ProfissionalService(ProfissionalSaudeRepository profissionalRepository,
                               ProcedimentoCirurgicoRepository procedimentoRepository) {
        this.profissionalRepository = profissionalRepository;
        this.procedimentoRepository = procedimentoRepository;
    }

    public List<ProfissionalSaude> buscarPorEspecialidade(String especialidade) {
        return profissionalRepository.buscarPorEspecialidade(especialidade);
    }

    public List<ProfissionalSaude> listarTodos() {
        return profissionalRepository.listarTodos();
    }

    public List<ProcedimentoCirurgico> buscarProcedimentosPorCirurgiaoId(Long id) {
        return procedimentoRepository.buscarPorCirurgiaoId(id);
    }
}