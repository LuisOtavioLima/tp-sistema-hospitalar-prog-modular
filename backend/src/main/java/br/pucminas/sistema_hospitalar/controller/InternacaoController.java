package br.pucminas.sistema_hospitalar.controller;

import br.pucminas.sistema_hospitalar.dto.AltaRequest;
import br.pucminas.sistema_hospitalar.dto.InternacaoRequest;
import br.pucminas.sistema_hospitalar.dto.InternacaoUpdateRequest;
import br.pucminas.sistema_hospitalar.model.Internacao;
import br.pucminas.sistema_hospitalar.service.InternacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internacoes")
public class InternacaoController {

    private final InternacaoService service;

    public InternacaoController(InternacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Internacao> criar(@Valid @RequestBody InternacaoRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dados));
    }

    @GetMapping
    public ResponseEntity<List<Internacao>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Internacao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Internacao> atualizar(@PathVariable Long id, @Valid @RequestBody InternacaoUpdateRequest dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @PostMapping("/{id}/alta")
    public ResponseEntity<Internacao> registrarAlta(@PathVariable Long id, @Valid @RequestBody AltaRequest dados) {
        return ResponseEntity.ok(service.registrarAlta(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
