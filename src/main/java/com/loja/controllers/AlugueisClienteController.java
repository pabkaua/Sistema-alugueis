package com.loja.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AlugueisClienteController {

    @FXML
    private Button botaoBuscar;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<?, ?> colunaDataDevolucao;

    @FXML
    private TableColumn<?, ?> colunaDataLocacao;

    @FXML
    private TableColumn<?, ?> colunaID;

    @FXML
    private TableColumn<?, ?> colunaItem;

    @FXML
    private ComboBox<?> listaColuna;

    @FXML
    private TableView<?> tabelaAlugueis;

    @FXML
    void buscarAluguel(ActionEvent event) {

    }

}