package com.loja.controllers;

import com.loja.model.Categoria;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CategAdminController {

    @FXML
    private TextField IdCategoria;

    @FXML
    private TextField NomeCategoria;

    @FXML
    private Button botaoBuscar;

    @FXML
    private Button botaoCateAdd;

    @FXML
    private Button botaoEditar;

    @FXML
    private Button botaoExcluir;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<Categoria, String> colunaID;

    @FXML
    private TableColumn<Categoria, String> colunaNome;

    @FXML
    private TableColumn<Categoria, String> colunaSelecionar;

    @FXML
    private ComboBox<String> listaColuna;

    @FXML
    private TableView<Categoria> tabela;

    private ILojaFacade facade;

    private final ObservableList<Categoria> dados = FXCollections.observableArrayList();
    private FilteredList<Categoria> filtrados;

    @FXML
    void buscar(ActionEvent event) {

    }

}