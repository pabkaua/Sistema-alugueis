package com.loja.business.interfaces;

import com.loja.model.ContratoAluguel;

import java.time.LocalDate;
import java.util.Map;

public interface IContratoBusiness {

    ContratoAluguel registrarAluguel(String clienteId, String itemId, LocalDate dataRetirada, LocalDate dataPrevDevolucao);

    ContratoAluguel buscar(String id);

    ContratoAluguel processarDevolucao(String contratoId);

    Map<String,ContratoAluguel> listarAtivos();

    Map<String,ContratoAluguel> listarPorCliente(String clienteId);

    Map<String,ContratoAluguel> listar();

    void atualizar(ContratoAluguel contrato);

    public void salvarDados();
}

