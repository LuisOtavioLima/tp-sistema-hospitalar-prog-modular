package br.pucminas.sistema_hospitalar.controller;

import br.pucminas.sistema_hospitalar.model.ProfissionalSaude;
import br.pucminas.sistema_hospitalar.model.ProcedimentoCirurgico;
import br.pucminas.sistema_hospitalar.service.ProfissionalService; // Importe o service correto da sua arquitetura

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    // Injeção de dependência via construtor (recomendada pelo Spring)
    public ProfissionalController(ProfissionalService profissionalService) {
        this.profissionalService = profissionalService;
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalSaude>> listarProfissionais(
            @RequestParam(required = false) String especialidade) {
        
        List<ProfissionalSaude> lista;
        
        if (especialidade != null && !especialidade.isBlank()) {
            lista = profissionalService.buscarPorEspecialidade(especialidade);
        } else {
            lista = profissionalService.listarTodos();
        }
        
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}/procedimentos")
    public ResponseEntity<List<ProcedimentoCirurgico>> listarProcedimentosPorProfissional(
            @PathVariable Long id) {
        
        List<ProcedimentoCirurgico> procedimentos = profissionalService.buscarProcedimentosPorCirurgiaoId(id);
        
        // Retorna 204 No Content se a lista vier vazia, ou 200 OK com os dados
        if (procedimentos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(procedimentos);
    }
}