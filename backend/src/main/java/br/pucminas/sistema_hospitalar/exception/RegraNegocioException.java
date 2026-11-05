package br.pucminas.sistema_hospitalar.exception;

public class RegraNegocioException extends RuntimeException {
    
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
