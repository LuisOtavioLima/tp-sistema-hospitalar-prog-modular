package br.pucminas.sistema_hospitalar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Tratamento para os erros de validação (@Valid do CPF, Email, NotBlank, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErrosValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        
        // Pega todos os campos que falharam e as respetivas mensagens definidas na classe Paciente
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros); // Retorna 400
    }

    // 2. Tratamento para as regras de negócio violadas exigidas no roteiro
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<String> tratarRegraNegocio(RegraNegocioException ex) {
        // Retorna 400 (Bad Request) com a mensagem clara, conforme o roteiro
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); 
    }

    // 3. Tratamento genérico para o "Paciente não encontrado" que definimos no Service
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> tratarErrosGerais(RuntimeException ex) {
        if (ex.getMessage().contains("não encontrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // Retorna 404
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno no servidor."); // Retorna 500
    }
}
