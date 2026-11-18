package br.com.fiap.ms.atendimentos.exceptions;

public class ServicoIndisponivelException extends RuntimeException {

    public ServicoIndisponivelException(String message) {
        super(message);
    }
}
