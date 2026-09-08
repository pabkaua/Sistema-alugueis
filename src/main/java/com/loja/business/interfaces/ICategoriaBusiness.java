package com.loja.business.interfaces;

import com.loja.model.Categoria;

import java.util.Map;

public interface ICategoriaBusiness {
    public void cadastrar(Categoria c);

    public Categoria buscar(String id);

    public void atualizar(Categoria categoria);

    public Map<String, Categoria> listar();

    public void deletar(String id);

    public void salvarDados();
}
