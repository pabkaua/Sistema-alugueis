package com.loja.repositories.interfaces;
import com.loja.model.Categoria;
import com.loja.model.Fornecedor;
import com.loja.model.Item;
import java.util.Map;

public interface IItemRepository {
    public void salvar(Item item);
    public Item buscar(String id);

    // O conjunto chave-valor vai ser String-Objeto, sendo a String o ID para o hashmap
    public Map<String, Item> listar();
    public Map<String, Item> listar(String status);
    public Map<String, Item> listar(Categoria categoria);
    public Map<String, Item> listar(Fornecedor fornecedor);

    public boolean atualizar(Item item);
    public boolean deletar(String id);

    public void carregarDados();
    public void salvarDados();
}
