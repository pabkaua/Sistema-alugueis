package com.loja.repositories;

import java.util.Collections;
import java.util.Map;

import com.loja.model.Categoria;
import com.loja.repositories.interfaces.ICategoriaRepository;

public class CategoriaRepositoryFake implements ICategoriaRepository {
    private Map<String, Categoria> categorias;

    @Override
    public void salvar(Categoria categoria) {
        categorias.put(categoria.getId(), categoria);
    }

    @Override
    public Categoria buscar(String id) {
        return categorias.get(id);
    }

    @Override
    public Map<String, Categoria> listar() {
        return Collections.unmodifiableMap(this.categorias);
    }

    @Override
    public boolean atualizar(Categoria categoria) {
        if (this.categorias.containsKey(categoria.getId())) {
            categorias.put(categoria.getId(), categoria);
            return true;
        }
        return false;
    }

    @Override
    public boolean deletar(String id) {
        if (this.categorias.containsKey(id)) {
            categorias.remove(id);
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