package com.loja.controllers;

import com.loja.controllers.admin.GerenciarCategoriasController;
import com.loja.controllers.admin.GerenciarItensController;
import com.loja.controllers.admin.GerenciarUsuariosController;
import com.loja.model.Administrador;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.io.IOException;

public class SidebarAdminController {

  private ILojaFacade facade;
  private Administrador usuarioLogado;

  @FXML private Button botaoDashboard;
  @FXML private Button btnAdminGerenciarUsuarios;
  @FXML private Button btnAdminGerenciarItens;
  @FXML private Button btnAdminGerenciarCategorias;
  @FXML private Button btnAdminGerenciarFornecedores;

  // Relatorios
  @FXML private Button btnAdminRelatorios;
  @FXML private VBox submenuAdminRelatorios;
  @FXML private Button btnAdminRelatorioAlugueisAtivos;
  @FXML private Button btnAdminRelatorioAluguelCliente;
  @FXML private Button btnAdminRelatorioFinanceiro;
  @FXML private Button btnAdminRelatorioItensDisponiveis;

  @FXML private Button botaoLogout;

  // Sidebar
  @FXML private StackPane conteudo;
  @FXML private VBox sidebarVbox;
  @FXML private Label tituloLoja;

  @FXML 
  public void initialize(){
    submenuAdminRelatorios.managedProperty().bind(submenuAdminRelatorios.visibleProperty());
  }

  public void setContexto(ILojaFacade facade, Administrador usuario){
    this.facade = facade;
    this.usuarioLogado = usuario;
  }

  @FXML
  void abrirMenuRelatorios(ActionEvent event){
    boolean estaVisivel = submenuAdminRelatorios.isVisible();
    submenuAdminRelatorios.setVisible(!estaVisivel);

    if(!estaVisivel){
      btnAdminRelatorios.setText("⌃   " + "Relatórios");
    } else {
      btnAdminRelatorios.setText("⌄   " + "Relatórios");
    }
  }

  @FXML
  void navegarDashboard(ActionEvent event){
    // carregar uma tela de dashboard
  }

  @FXML
  void navegarGerenciarUsuarios(ActionEvent event){
    try {
      java.net.URL url = getClass().getResource("/ui/admin/gerenciar-usuarios.fxml");
      if (url == null){
        Alerta.exibirAlertaErro("Erro: 404 Not Found", " Arquivo gerenciar-usuarios.fxml não encontrado");
        return;
      }

      javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(url);
      javafx.scene.Node tela = loader.load();

      GerenciarUsuariosController controller = loader.getController();
      controller.setContexto(this.facade);
      conteudo.getChildren().setAll(tela);
    } catch (IOException e){
      Alerta.exibirAlertaErro("Erro de carregamento", "Erro ao carregar a tela de usuarios");
    }
  }

  @FXML
  void navegarGerenciarItens(ActionEvent event) {
    try {
      java.net.URL url = getClass().getResource("/ui/admin/gerenciar-itens.fxml");
      if (url == null){
        Alerta.exibirAlertaErro("Erro: 404 Not Found", " Arquivo gerenciar-itens.fxml não encontrado");
        return;
      }

      javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(url);
      javafx.scene.Node tela = loader.load();

      GerenciarItensController controller = loader.getController();
      controller.setContexto(this.facade, conteudo);
      conteudo.getChildren().setAll(tela);
    } catch (IOException e){
      Alerta.exibirAlertaErro("Erro de carregamento", "Erro ao carregar a tela de itens");
    }
  }

  @FXML
  void navegarGerenciarCategorias(ActionEvent event) {
    try {
      java.net.URL url = getClass().getResource("/ui/admin/gerenciar-categorias.fxml");
      if (url == null){
        Alerta.exibirAlertaErro("Erro: 404 Not Found", " Arquivo gerenciar-categorias.fxml não encontrado");
        return;
      }

      javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(url);
      javafx.scene.Node tela = loader.load();

      GerenciarCategoriasController controller = loader.getController();
      controller.setFacade(this.facade);
      conteudo.getChildren().setAll(tela);
    } catch (IOException e){
      Alerta.exibirAlertaErro("Erro de carregamento", "Erro ao carregar a tela de itens");
    }
  }

  @FXML
  void navegarGerenciarFornecedores(ActionEvent event) {
    // carregar uma tela de fornecedores
  }

  @FXML
  void fazerLogout(ActionEvent event){
    try{
      URL url = getClass().getResource("/ui/LoginScreen.fxml");
      if (url == null) {
        Alerta.exibirAlertaErro("Erro de navegação", "O arquivo LoginScreen.fxml não foi localizado.");
        return;
      }

      FXMLLoader loader = new FXMLLoader(url);
      Parent root = loader.load();

      LoginController controller = loader.getController();
      controller.setFacade(this.facade);

      Stage stage = (Stage) botaoLogout.getScene().getWindow();
      stage.setTitle("Login - Aluga Tudo");

      if (stage.getScene() != null) {
        stage.getScene().setRoot(root);
      } else {
        stage.setScene(new Scene(root));
      }


    } catch (IOException e) {
      Alerta.exibirAlertaErro("Erro de navegação", "Erro ao carregar a tela de login.");
    }
  }
}
