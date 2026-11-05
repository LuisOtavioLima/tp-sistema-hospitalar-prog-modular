package br.pucminas.sistema_hospitalar.controller;

import br.pucminas.sistema_hospitalar.dto.ConsultaRequest;
import br.pucminas.sistema_hospitalar.dto.ConsultaUpdateRequest;
import br.pucminas.sistema_hospitalar.dto.ObservacoesRequest;
import br.pucminas.sistema_hospitalar.dto.ReagendamentoRequest;
import br.pucminas.sistema_hospitalar.model.Consulta;
import br.pucminas.sistema_hospitalar.model.SituacaoConsulta;
import br.pucminas.sistema_hospitalar.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Consulta> agendar(@Valid @RequestBody ConsultaRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agendar(dados));
    }

    @GetMapping
    public ResponseEntity<List<Consulta>> listar(@RequestParam(required = false) SituacaoConsulta situacao,
                                                 @RequestParam(required = false) Long pacienteId,
                                                 @RequestParam(required = false) Long profissionalId) {
        return ResponseEntity.ok(service.listar(situacao, pacienteId, profissionalId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consulta> atualizar(@PathVariable Long id, @Valid @RequestBody ConsultaUpdateRequest dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<Consulta> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmar(id));
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Consulta> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(service.iniciar(id));
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<Consulta> concluir(@PathVariable Long id,
                                             @RequestBody(required = false) ObservacoesRequest dados) {
        return ResponseEntity.ok(service.concluir(id, dados == null ? null : dados.observacoes()));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Consulta> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @PostMapping("/{id}/nao-compareceu")
    public ResponseEntity<Consulta> registrarNaoComparecimento(@PathVariable Long id) {
        return ResponseEntity.ok(service.registrarNaoComparecimento(id));
    }

    @PostMapping("/{id}/reagendar")
    public ResponseEntity<Consulta> reagendar(@PathVariable Long id, @Valid @RequestBody ReagendamentoRequest dados) {
        return ResponseEntity.ok(service.reagendar(id, dados));
    }

    @PutMapping("/{id}/observacoes")
    public ResponseEntity<Consulta> registrarObservacoes(@PathVariable Long id, @RequestBody ObservacoesRequest dados) {
        return ResponseEntity.ok(service.registrarObservacoes(id, dados.observacoes()));
    }
}
