package com.loja.business.interfaces;

import java.util.Map;
import com.loja.model.Fornecedor;

public interface IFornecedorBusiness {

    public void cadastrar(Fornecedor f);

    public Fornecedor buscar(String id);

    public void atualizar(Fornecedor fornecedor);

    public Map<String, Fornecedor> listar();

    public void deletar(String id);

    public void salvarDados();
}
