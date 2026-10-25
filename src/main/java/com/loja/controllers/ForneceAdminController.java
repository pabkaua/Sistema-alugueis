package com.loja.controllers;

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
    private TableColumn<?, ?> colunaCNPJ;

    @FXML
    private TableColumn<?, ?> colunaID;

    @FXML
    private TableColumn<?, ?> colunaNome;

    @FXML
    private TableColumn<?, ?> colunaTelefone;

    @FXML
    private ComboBox<?> listaColuna;

    @FXML
    private TableView<?> tabela;

    @FXML
    void buscar(ActionEvent event) {

    }

}
