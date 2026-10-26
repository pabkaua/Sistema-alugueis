package com.loja.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.loja.padraofacade.interfaces.ILojaFacade;
import com.loja.model.Usuario;

public class GerenciarUsuariosController {
    // Funcao de Listar
    @FXML private TableView<Usuario> adminTabelaUsuarios;
    @FXML private TableColumn<Usuario, String> adminColunaId;
    @FXML private TableColumn<Usuario, String> adminColunaNome;
    @FXML private TableColumn<Usuario, String> adminColunaLogin;
    @FXML private TableColumn<Usuario, String> adminColunaPerfil;
    @FXML private TableColumn<Usuario, String> adminColunaSituacao;

    // Funcoes de buscar, cadastrar, editar e desativar usuarios
    @FXML private TextField campoBuscaAdminUsuario;
    @FXML private Button btnAdminAdicionarUsuario;
    @FXML private Button btnAdminEditarUsuario;
    @FXML private Button btnAdminDesativarUsuario;
    private ILojaFacade facade;
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

    }
}
