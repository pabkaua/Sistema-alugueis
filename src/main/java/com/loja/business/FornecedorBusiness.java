package com.loja.business;

import com.loja.business.interfaces.IFornecedorBusiness;
import com.loja.model.Fornecedor;
import com.loja.repositories.interfaces.IFornecedorRepository;
import java.util.Map;

public class FornecedorBusiness implements IFornecedorBusiness {
    IFornecedorRepository repo;

    public FornecedorBusiness(IFornecedorRepository repo){
        this.repo = repo;
    }
    
    public Map<String, Fornecedor> listar() {
        return repo.listar();
    }

    public void cadastrar(Fornecedor f){
        if (f == null){ // se o objeto foi preenchido
            throw new RuntimeException("Não foi possivel cadastrar o objeto fornecedor!");
        } else if (repo.buscar(f.getId()) != null) { // se ja existe com o id
            throw new RuntimeException("Já existe outro fornecedor com esse ID: " + repo.buscar(f.getId()).getNome());
        } else if (f.getNome() == null || f.getNome().isBlank()) { // se o nome tá ok
            throw new RuntimeException("Não foi possível cadastrar o fornecedor: Falha no nome");
        } else if (f.getId() == null || f.getId().isBlank()) { // se o id tá ok
            throw new RuntimeException("Não foi possível cadastrar o fornecedor: Falha no ID");
        }

        boolean nomeExiste = repo.listar()
                .values()
                .stream()
                .anyMatch(fornecedor -> fornecedor.getNome().equalsIgnoreCase(f.getNome()));

        if (nomeExiste) { // se já existe com o nome
            throw new RuntimeException("Já existe um fornecedor com o nome: " + f.getNome());
        }

        repo.salvar(f);
    }

    public Fornecedor buscar(String id){
        if (id == null){
            throw new RuntimeException("Id do fornecedor inválido");
        }

        Fornecedor fornecedor = repo.buscar(id);
        if (fornecedor == null){
            throw new RuntimeException("Fornecedor não encontrado!");
        }

        return fornecedor;
    }

    public void atualizar(Fornecedor fornecedor){
        if (fornecedor == null){
            throw new RuntimeException("Fornecedor inválido!");
        } else if (fornecedor.getNome() == null || fornecedor.getNome().isBlank()){
            throw new RuntimeException("Nome do fornecedor inválido!");
        }
        if (!repo.atualizar(fornecedor)){
            throw new RuntimeException("Não foi possível atualizar!");
        }
    }

    public void deletar(String id){
        if (repo.buscar(id) == null || id.isBlank()){
            throw new RuntimeException("Fornecedor não encontrado: " + id);
        } else if (repo.buscar(id).hasHistorico()){
            throw new RuntimeException("O fornecedor não pode ser excluído, tem histórico");
        } else if (repo.listar().values().stream().noneMatch(forn -> forn.getId().equals(id))){
            throw new RuntimeException("Não existe um fornecedor com esse id");
        }
        repo.deletar(id);
    }

    public void salvarDados(){
        this.repo.salvarDados();
    }
}