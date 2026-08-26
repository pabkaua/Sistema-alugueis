package com.loja.repositories;

import com.loja.model.Categoria;
import com.loja.model.Fornecedor;
import com.loja.model.Item;
import com.loja.repositories.interfaces.IItemRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemRepositoryFake implements IItemRepository {

    private Map<String, Item> itens = new HashMap<>();

    @Override
    public void salvar(Item item) {
        itens.put(item.getId(), item);
    }

    @Override
    public Item buscar(String id) {
        return itens.get(id);
    }

    @Override
    public Map<String, Item> listar() {
        return Collections.unmodifiableMap(itens);
    }

    @Override
    public Map<String, Item> listar(String status) {
        return itens.entrySet().stream()
                .filter(e -> e.getValue().getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Item> listar(Categoria categoria) {
        return itens.entrySet().stream()
                .filter(e -> e.getValue().getCategoria().getId().equals(categoria.getId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Item> listar(Fornecedor fornecedor) {
        return itens.entrySet().stream()
                .filter(e -> e.getValue().getFornecedor().getId().equals(fornecedor.getId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean atualizar(Item item) {
        if (!itens.containsKey(item.getId())) return false;
        itens.put(item.getId(), item);
        return true;
    }

    @Override
    public boolean deletar(String id) {
        return itens.remove(id) != null;
    }

    @Override public void carregarDados() {}
    @Override public void salvarDados() {}
}