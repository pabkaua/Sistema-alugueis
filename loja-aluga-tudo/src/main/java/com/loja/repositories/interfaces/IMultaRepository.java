package com.loja.repositories.interfaces;

import com.loja.model.Cliente;
import com.loja.model.Multa;
import java.util.Map;

public interface IMultaRepository {

    public void salvar(Multa multa);
    public Multa buscar(String id);
    
    public Map<String, Multa> listar();
    public Map<String, Multa> listar(Cliente cliente);
    public Map<String, Multa> listar(String status);

    public boolean atualizar(Multa multa);
    public boolean deletar(String id);

    public void carregarDados();
    public void salvarDados();
}