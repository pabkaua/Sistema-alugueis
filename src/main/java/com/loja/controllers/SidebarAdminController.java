package com.loja.controllers;

import com.loja.model.Administrador;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SidebarAdminController {

  private ILojaFacade facade;
  private Administrador usuarioLogado;

  @FXML private Button botaoDashboard;
  @FXML private Button botaoLogout;

  // Gerenciar Usuarios
  @FXML private Button btnAdminGerenciarUsuarios;
  @FXML private VBox submenuAdminGerenciarUsuarios;
  @FXML private Button btnAdminAtualizarUsuario;
  @FXML private Button btnAdminCadastrarUsuario;
  @FXML private Button btnAdminDesativarUsuario;
  @FXML private Button btnAdminListarUsuarios;

  // Gerenciar Itens
  @FXML private Button btnAdminGerenciarItens;
  @FXML private VBox submenuAdminGerenciarItens;
  @FXML private Button btnAdminAtualizarItem;
  @FXML private Button btnAdminCadastrarItem;
  @FXML private Button btnAdminDeletarItem;
  @FXML private Button btnAdminListarItens;

  // Gerenciar Categorias
  @FXML private Button btnAdminGerenciarCategorias;
  @FXML private VBox submenuAdminGerenciarCategorias;
  @FXML private Button btnAdminAtualizarCategoria;
  @FXML private Button btnAdminCadastrarCategoria;
  @FXML private Button btnAdminDeletarCategoria;
  @FXML private Button btnAdminListarCategorias;

  // Gerenciar Fornecedores
  @FXML private Button btnAdminGerenciarFornecedores;
  @FXML private VBox submenuAdminGerenciarFornecedores;
  @FXML private Button btnAdminAtualizarFornecedor;
  @FXML private Button btnAdminCadastrarFornecedor;
  @FXML private Button btnAdminDeletarFornecedor;
  @FXML private Button btnAdminListarFornecedores;
  
  // Relatorios
  @FXML private Button btnAdminRelatorios;
  @FXML private VBox submenuAdminRelatorios;
  @FXML private Button btnAdminRelatorioAlugueisAtivos;
  @FXML private Button btnAdminRelatorioAluguelCliente;
  @FXML private Button btnAdminRelatorioFinanceiro;
  @FXML private Button btnAdminRelatorioItensDisponiveis;
  
  // Sidebar
  @FXML private StackPane conteudo;
  @FXML private VBox sidebarVbox;
  @FXML private Label tituloLoja;


  @FXML 
  public void initialize(){
    submenuAdminGerenciarUsuarios.managedProperty().bind(submenuAdminGerenciarUsuarios.visibleProperty());
    submenuAdminGerenciarItens.managedProperty().bind(submenuAdminGerenciarItens.visibleProperty());
    submenuAdminGerenciarCategorias.managedProperty().bind(submenuAdminGerenciarCategorias.visibleProperty());
    submenuAdminGerenciarFornecedores.managedProperty().bind(submenuAdminGerenciarFornecedores.visibleProperty());
    submenuAdminRelatorios.managedProperty().bind(submenuAdminRelatorios.visibleProperty());
  }

  public void setContexto(ILojaFacade facade, Administrador usuario){
    this.facade = facade;
    this.usuarioLogado = usuario;
  }

  // Menus
  private void alternarSubmenu(VBox submenu, Button botaoPrincipal, String titulo){
    boolean estaVisivel = submenu.isVisible();
    submenu.setVisible(!estaVisivel);

    if(!estaVisivel){
      botaoPrincipal.setText("⌃   " + titulo);
    } else {
      botaoPrincipal.setText("⌄   " + titulo);
    }
  }

  @FXML
  void abrirMenuUsuarios(ActionEvent event){
    alternarSubmenu(submenuAdminGerenciarUsuarios, btnAdminGerenciarUsuarios, "Gerenciar Usuários");
  }

  @FXML
  void abrirMenuItens(ActionEvent event){
    alternarSubmenu(submenuAdminGerenciarItens, btnAdminGerenciarItens, "Gerenciar Itens");
  }

  @FXML
  void abrirMenuCategorias(ActionEvent event){
    alternarSubmenu(submenuAdminGerenciarCategorias, btnAdminGerenciarCategorias, "Gerenciar Categorias");
  }

  @FXML
  void abrirMenuFornecedores(ActionEvent event){
    alternarSubmenu(submenuAdminGerenciarFornecedores, btnAdminGerenciarFornecedores, "Gerenciar Fornecedores");
  }

  @FXML
  void abrirMenuRelatorios(ActionEvent event){
    alternarSubmenu(submenuAdminRelatorios, btnAdminRelatorios, "Relatórios");
  }

  @FXML
  void fazerLogout(ActionEvent event){

  }

  @FXML
  void navegarDashBoard(ActionEvent event){

  }

}
