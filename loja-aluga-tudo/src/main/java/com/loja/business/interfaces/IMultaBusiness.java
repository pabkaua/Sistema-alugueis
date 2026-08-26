package com.loja.business.interfaces;

import com.loja.model.ContratoAluguel;
import com.loja.model.Multa;

import java.math.BigDecimal;
import java.util.Map;

public interface IMultaBusiness{

    void aplicar(ContratoAluguel contrato);

    void quitar(String multaId);

    Map<String, Multa> listarPorCliente(String clienteId);

    BigDecimal calcularAtraso(ContratoAluguel contrato);

    boolean possuiMultaPendente(String clienteId);

    Map<String, Multa> listar();
    
    Multa buscar(String Id);

    void atualizar(Multa multa);

    void deletarMulta(String Id);

    public void salvarDados();
}