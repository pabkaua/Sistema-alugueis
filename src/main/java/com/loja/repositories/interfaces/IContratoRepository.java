package com.loja.repositories.interfaces;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;

import java.util.Map;

public interface IContratoRepository {
    void salvar(ContratoAluguel contrato);

    ContratoAluguel buscar(String id);

    Map<String, ContratoAluguel> listar();

    Map<String, ContratoAluguel> listar(Cliente cliente);

    Map<String, ContratoAluguel> listar(String status);

    boolean atualizar(ContratoAluguel contrato);

    boolean deletar(String id);

    public void carregarDados();

    public void salvarDados();
}
