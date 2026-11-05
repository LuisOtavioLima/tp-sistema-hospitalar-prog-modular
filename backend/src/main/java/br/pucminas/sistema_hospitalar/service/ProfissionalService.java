package br.pucminas.sistema_hospitalar.service;

import br.pucminas.sistema_hospitalar.model.ProfissionalSaude;
import br.pucminas.sistema_hospitalar.model.ProcedimentoCirurgico;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfissionalService {

    // Injete seu Repository aqui (ex: ProfissionalRepository)

    public List<ProfissionalSaude> buscarPorEspecialidade(String especialidade) {
        // Lógica para buscar MedicoEspecialista pela string da especialidade
        return null; 
    }

    public List<ProfissionalSaude> listarTodos() {
        // Retorna findAll() do repository
        return null;
    }

    public List<ProcedimentoCirurgico> buscarProcedimentosPorCirurgiaoId(Long id) {
        // Lógica para buscar os procedimentos onde cirurgiao.id == id
        return null;
    }
}