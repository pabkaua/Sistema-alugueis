package com.loja;

import com.loja.business.*;
import com.loja.business.interfaces.*;
import com.loja.padraoFacade.LojaFacade;
import com.loja.padraoFacade.interfaces.ILojaFacade;
import com.loja.repositories.*;
import com.loja.repositories.interfaces.*;
import com.loja.ui.MenuLogin;

public class Main {
    public static void main(String[] args) {

        // camada de persistência
        IUsuarioRepository usuarioRepo = new UsuarioPersistenciaCSV("dados/usuarios.csv");
        IItemRepository itemRepo = new ItemPersistenciaCSV("dados/itens.csv");
        ICategoriaRepository categoriaRepo = new CategoriaPersistenciaCSV("dados/categorias.csv");
        IFornecedorRepository fornecedorRepo = new FornecedorPersistenciaCSV("dados/fornecedores.csv");
        IContratoRepository contratoRepo = new ContratoPersistenciaCSV("dados/contratos.csv");
        IMultaRepository multaRepo = new MultaPersistenciaCSV("dados/multas.csv");

        // camada de negócio
        IUsuarioBusiness usuarioBusiness = new UsuarioBusiness(usuarioRepo);
        ICategoriaBusiness categoriaBusiness = new CategoriaBusiness(categoriaRepo);
        IFornecedorBusiness fornecedorBusiness = new FornecedorBusiness(fornecedorRepo);
        IItemBusiness itemBusiness = new ItemBusiness(itemRepo, categoriaRepo, fornecedorRepo);
        IContratoBusiness contratoBusiness = new ContratoBusiness(contratoRepo, itemBusiness, usuarioBusiness);
        IMultaBusiness multaBusiness = new MultaBusiness(multaRepo);

        // facade
        ILojaFacade facade = new LojaFacade(usuarioBusiness, itemBusiness, categoriaBusiness, fornecedorBusiness, contratoBusiness, multaBusiness);

        // inicia o sistema
        new MenuLogin(facade).iniciar();
    }
}
