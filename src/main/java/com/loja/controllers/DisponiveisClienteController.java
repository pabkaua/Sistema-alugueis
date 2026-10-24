package com.loja.controllers;

import com.loja.model.Cliente;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DisponiveisClienteController {

    ILojaFacade facade;

    @FXML
    private Button botaoBuscar;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<?, ?> colunaCategoria;

    @FXML
    private TableColumn<?, ?> colunaID;

    @FXML
    private TableColumn<?, ?> colunaItem;

    @FXML
    private TableColumn<?, ?> colunaValor;

    @FXML
    private ComboBox<?> listaColuna;

    @FXML
    private TableView<?> tabela;

    public void setDados(ILojaFacade facade) {
        this.facade = facade;
    }

    @FXML
    void buscar(ActionEvent event) {
        // a fazer
    }

}
