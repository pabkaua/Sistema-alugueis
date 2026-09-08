package com.loja.repositories.interfaces;
import com.loja.model.Categoria;
import java.util.Map;

public interface ICategoriaRepository {
    public void salvar(Categoria categoria);
    public Categoria buscar(String id);

    // O conjunto chave-valor vai ser String-Objeto, sendo a String o ID para o hashmap
    public Map<String, Categoria> listar();

    public boolean atualizar(Categoria categoria);
    public boolean deletar(String id);

    public void carregarDados();
    public void salvarDados();
}
