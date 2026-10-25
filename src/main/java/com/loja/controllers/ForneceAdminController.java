package com.loja.controllers;

import com.loja.model.Fornecedor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ForneceAdminController {

    @FXML
    private Button botaoBuscar11;

    @FXML
    private Button botaoNovoForne;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<Fornecedor, String> colunaCNPJ;

    @FXML
    private TableColumn<Fornecedor, String> colunaID;

    @FXML
    private TableColumn<Fornecedor, String>colunaNome;

    @FXML
    private TableColumn<Fornecedor, String> colunaTelefone;

    @FXML
    private ComboBox<String> listaColuna;

    @FXML
    private TableView<Fornecedor> tabela;

    @FXML
    void buscar(ActionEvent event) {

    }

}
