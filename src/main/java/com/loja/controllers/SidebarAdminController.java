package com.loja.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SidebarAdminController {

  @FXML private Button botaoDashboard;
  @FXML private Button botaoLogout;
  @FXML private Button btnAdminAtualizarItem;
  @FXML private Button btnAdminAtualizarUsuario;
  @FXML private Button btnAdminCadastrarItem;
  @FXML private Button btnAdminCadastrarUsuario;
  @FXML private Button btnAdminDesativarUsuario;
  @FXML private Button btnAdminGerenciarItens;
  @FXML private Button btnAdminGerenciarUsuarios;
  @FXML private Button btnAdminListarUsuarios;
  @FXML private Button btnAdminListarItens;
  @FXML private StackPane conteudo;
  @FXML private VBox sidebarVbox;
  @FXML private VBox submenuAdminGerenciarItens;
  @FXML private VBox submenuAdminGerenciarUsuarios;
  @FXML private Label tituloLoja;


  @FXML void fazerLogout(ActionEvent event){

  }

  @FXML void navegarDashBoard(ActionEvent event){

  }

}
