package com.loja.business;

public class BusinessException extends RuntimeException {
    public BusinessException(String message){
        super(message);
    }
}
