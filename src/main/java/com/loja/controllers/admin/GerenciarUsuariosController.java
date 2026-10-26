package com.loja.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
        adminColunaId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        adminColunaNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        adminColunaLogin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLogin()));
        adminColunaPerfil.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPerfil()));

        adminColunaSituacao.setCellValueFactory(c -> {
            return new SimpleStringProperty(c.getValue().isAtivo() ? "Ativo" : "Inativo");
        });

        btnAdminEditarUsuario.disableProperty().bind(adminTabelaUsuarios.getSelectionModel().selectedItemProperty().isNull());
        btnAdminDesativarUsuario.disableProperty().bind(adminTabelaUsuarios.getSelectionModel().selectedItemProperty().isNull());

        adminTabelaUsuarios.setPlaceholder(new Label("Nenhum usuário encontrado."));
        filtroDeBusca();

    }

    private void carregarDados(){
        if(facade != null && facade.listarUsuario() != null){
            listaUsuarios.setAll(facade.listarUsuario().values());
        }
    }
    public void setContexto(ILojaFacade facade){
        this.facade = facade;
        carregarDados();
    }

    private void filtroDeBusca(){
        FilteredList<Usuario> listaFiltrada = new FilteredList<>(listaUsuarios, u -> true);
        campoBuscaAdminUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(usuario -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String termo = newValue.toLowerCase();
                return usuario.getNome().toLowerCase().contains(termo)
                        || usuario.getLogin().toLowerCase().contains(termo)
                        || usuario.getId().toLowerCase().contains(termo);
            });
        });

        SortedList<Usuario> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(adminTabelaUsuarios.comparatorProperty());
        adminTabelaUsuarios.setItems(listaOrdenada);
    }
}
