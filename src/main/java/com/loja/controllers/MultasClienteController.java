package com.loja.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MultasClienteController {

    @FXML
    private Button botaoBuscar;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<?, ?> colunaAtraso;

    @FXML
    private TableColumn<?, ?> colunaID;

    @FXML
    private TableColumn<?, ?> colunaIdContrato;

    @FXML
    private TableColumn<?, ?> colunaItem;

    @FXML
    private TableColumn<?, ?> colunaValor;

    @FXML
    private ComboBox<?> listaColuna;

    @FXML
    private TableView<?> tabelaMultas;

    @FXML
    void buscarMulta(ActionEvent event) {
        // a fazer
    }

}
