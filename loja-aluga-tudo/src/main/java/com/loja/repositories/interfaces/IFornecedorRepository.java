package com.loja.repositories.interfaces;
import com.loja.model.Fornecedor;
import java.util.Map;

public interface IFornecedorRepository {
    public void salvar(Fornecedor fornecedor);
    public Fornecedor buscar(String id);
    public Map<String, Fornecedor> listar();
    public boolean atualizar(Fornecedor fornecedor);
    public boolean deletar(String id);
    public void carregarDados();
    public void salvarDados();
}