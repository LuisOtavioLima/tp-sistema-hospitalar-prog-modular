package br.pucminas.sistema_hospitalar.controller;

import br.pucminas.sistema_hospitalar.dto.AtendimentoEmergenciaRequest;
import br.pucminas.sistema_hospitalar.dto.IniciarEmergenciaRequest;
import br.pucminas.sistema_hospitalar.model.AtendimentoEmergencia;
import br.pucminas.sistema_hospitalar.model.SituacaoEmergencia;
import br.pucminas.sistema_hospitalar.service.AtendimentoEmergenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas/emergencias")
public class AtendimentoEmergenciaController {

    private final AtendimentoEmergenciaService service;

    public AtendimentoEmergenciaController(AtendimentoEmergenciaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AtendimentoEmergencia> registrar(@Valid @RequestBody AtendimentoEmergenciaRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dados));
    }

    @GetMapping
    public ResponseEntity<List<AtendimentoEmergencia>> listar(
            @RequestParam(defaultValue = "AGUARDANDO") SituacaoEmergencia situacao) {
        return ResponseEntity.ok(service.listarPorSituacao(situacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoEmergencia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<AtendimentoEmergencia> iniciar(@PathVariable Long id,
                                                         @Valid @RequestBody IniciarEmergenciaRequest dados) {
        return ResponseEntity.ok(service.iniciar(id, dados.profissionalId()));
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<AtendimentoEmergencia> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(service.concluir(id));
    }
}
