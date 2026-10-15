package com.loja.exceptions;

public class MultaNaoEncontradaException extends RuntimeException {

    public MultaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
