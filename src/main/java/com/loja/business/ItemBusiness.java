package com.loja.business;

import com.loja.business.interfaces.IItemBusiness;
import com.loja.model.Categoria;
import com.loja.model.Fornecedor;
import com.loja.model.Item;
import com.loja.repositories.interfaces.ICategoriaRepository;
import com.loja.repositories.interfaces.IFornecedorRepository;
import com.loja.repositories.interfaces.IItemRepository;

import java.util.Map;

public class ItemBusiness implements IItemBusiness {
    private IItemRepository repo;
    private ICategoriaRepository catRepo;
    private IFornecedorRepository fornRepo;

    public ItemBusiness(IItemRepository repo, ICategoriaRepository catRepo, IFornecedorRepository fornRepo){
        this.repo = repo;
        this.catRepo = catRepo;
        this.fornRepo = fornRepo;
    }

    public void cadastrar(Item i){
        if (repo.buscar(i.getId()) != null){
            throw new RuntimeException("Item já cadastrado: " + i.getNome());
        }
        else if (catRepo.buscar(i.getCategoria().getId()) == null) {
            throw new RuntimeException("Categoria não cadastrada: " + i.getCategoria().getNome());
        }
        else if (fornRepo.buscar(i.getFornecedor().getId()) == null){
            throw new RuntimeException("Fornecedor não cadastrado: " + i.getFornecedor().getNome());
        }

        if (i.getStatus().isBlank() || i.getStatus() == null){
            i.setStatus("DISPONIVEL");
        }
        repo.salvar(i);
    };

    public Item buscar(String id){
        Item item = repo.buscar(id);
        if(item == null){
            throw new RuntimeException("Item não encontrado: " + id);
        }
        return item;
    };

    public Map<String, Item> listar(){
        return repo.listar();
    };

    public Map<String, Item> listarPorStatus(String status){
        return repo.listar(status);
    };

    public Map<String, Item> listarPorCategoria(Categoria categoria){
        if (categoria == null){
            throw new RuntimeException("Categoria inválida!");
        } else if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new RuntimeException("Nome da categoria inválida!");
        } else if (catRepo.buscar(categoria.getId()) == null){
            throw new RuntimeException("Categoria não cadastrada: " + categoria.getNome());
        }
        return repo.listar(categoria);
    };

    public Map<String, Item> listarPorFornecedor(Fornecedor fornecedor){
        if (fornecedor == null){
            throw new RuntimeException("Fornecedor inválido!");
        } else if (fornecedor.getNome() == null || fornecedor.getNome().isBlank()){
            throw new RuntimeException("Nome do fornecedor inválido!");
        } else if (fornRepo.buscar(fornecedor.getId()) == null){
            throw new RuntimeException("Fornecedor não cadastrado: " + fornecedor.getNome());
        }
        return repo.listar(fornecedor);
    }

    public void atualizar(Item item){
        if (item == null){
            throw new RuntimeException("Item inválido!");
        }
        if (!repo.atualizar(item)){
            throw new RuntimeException("Não foi possível atualizar!");
        };
    };

    public void deletar(String id){
        if (repo.buscar(id) == null){
            throw new RuntimeException("Item não encontrado: " + id);
        }
        if (repo.buscar(id).hasHistorico()){
            throw new RuntimeException("O item não pode ser excluido, tem histórico");
        }
        repo.deletar(id);
    };

    public void salvarDados(){
        this.repo.salvarDados();
    }
}
