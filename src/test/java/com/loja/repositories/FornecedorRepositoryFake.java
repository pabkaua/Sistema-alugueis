package com.loja.repositories;

import java.util.Collections;
import java.util.Map;

import com.loja.model.Fornecedor;
import com.loja.repositories.interfaces.IFornecedorRepository;

public class FornecedorRepositoryFake implements IFornecedorRepository{
    private Map<String, Fornecedor> fornecedores;

    @Override
    public void salvar(Fornecedor fornecedor) {
        fornecedores.put(fornecedor.getId(), fornecedor);
    }

    @Override
    public Fornecedor buscar(String id) {
        return fornecedores.get(id);
    }

    @Override
    public Map<String, Fornecedor> listar() {
        return Collections.unmodifiableMap(this.fornecedores);
    }

    @Override
    public boolean atualizar(Fornecedor fornecedor) {
        if (this.fornecedores.containsKey(fornecedor.getId())) {
            fornecedores.put(fornecedor.getId(), fornecedor);
            return true;
        }
        return false;
    }

    @Override
    public boolean deletar(String id) {
        if (this.fornecedores.containsKey(id)) {
            fornecedores.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void carregarDados() {

    }
    

    @Override
    public void salvarDados() {

    }
}
