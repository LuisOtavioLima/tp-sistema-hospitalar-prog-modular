package br.pucminas.sistema_hospitalar.controller;

import br.pucminas.sistema_hospitalar.dto.QuartoRequest;
import br.pucminas.sistema_hospitalar.model.Quarto;
import br.pucminas.sistema_hospitalar.service.QuartoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quartos")
public class QuartoController {

    private final QuartoService service;

    public QuartoController(QuartoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Quarto> criar(@Valid @RequestBody QuartoRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dados));
    }

    @GetMapping
    public ResponseEntity<List<Quarto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Quarto>> listarDisponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quarto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quarto> atualizar(@PathVariable Long id, @Valid @RequestBody QuartoRequest dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
