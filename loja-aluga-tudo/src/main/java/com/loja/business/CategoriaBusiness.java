package com.loja.business;

import com.loja.business.interfaces.ICategoriaBusiness;
import com.loja.model.Categoria;
import com.loja.repositories.interfaces.ICategoriaRepository;

import java.util.Map;

public class CategoriaBusiness implements ICategoriaBusiness {
    ICategoriaRepository repo;

    public CategoriaBusiness(ICategoriaRepository repo){
        this.repo = repo;
    }

    public void cadastrar(Categoria c){
        if (c == null){ // se o objeto foi preenchido
            throw new RuntimeException("Não foi possivel cadastrar o objeto categoria!");
        } else if (repo.buscar(c.getId()) != null) { // se ja existe com o id
            throw new RuntimeException("Já existe outra categoria com esse ID: " + repo.buscar(c.getId()).getNome());
        } else if (c.getNome() == null || c.getNome().isBlank()) { // se o nome tá ok
            throw new RuntimeException("Não foi possível cadastrar a categoria: Falha no nome");
        } else if (c.getId() == null || c.getId().isBlank()) { // se o id tá ok
            throw new RuntimeException("Não foi possível cadastrar a categoria: Falha no ID");
        }

        boolean nomeExiste = repo.listar()
                .values()
                .stream()
                .anyMatch(cat -> cat.getNome().equalsIgnoreCase(c.getNome()));

        if (nomeExiste) { // se já existe com o nome
            throw new RuntimeException("Já existe uma categoria com o nome: " + c.getNome());
        }

        repo.salvar(c);
    };

    public Categoria buscar(String id){
        if (id == null){
            throw new RuntimeException("Id da categoria inválido");
        }

        Categoria categoria = repo.buscar(id);
        if (categoria == null){
            throw new RuntimeException("Categoria não encontrada!");
        }

        return categoria;
    };

    public void atualizar(Categoria categoria){
        if (categoria == null){
            throw new RuntimeException("Categoria inválida!");
        } else if (categoria.getNome() == null || categoria.getNome().isBlank()){
            throw new RuntimeException("Nome da categoria inválido!");
        }
        if (!repo.atualizar(categoria)){
            throw new RuntimeException("Não foi possível atualizar!");
        };
    };

    public Map<String, Categoria> listar(){
        return repo.listar();
    };

    public void deletar(String id){
        if (repo.buscar(id) == null || id.isBlank()){
            throw new RuntimeException("Categoria não encontrada: " + id);
        } else if (repo.buscar(id).hasHistorico()){
            throw new RuntimeException("A categoria não pode ser excluida, tem histórico");
        } else if (repo.listar().values().stream().noneMatch(cat -> cat.getId().equals(id))){
            throw new RuntimeException("Não existe uma categoria com esse id");
        }
        repo.deletar(id);
    };

    public void salvarDados(){
        this.repo.salvarDados();
    }
}