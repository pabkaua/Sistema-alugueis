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

public class MultasClienteController {

    Cliente usuario;
    ILojaFacade facade;

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

    public void setDados(Cliente usuario, ILojaFacade facade) {
        this.usuario = usuario;
        this.facade = facade;
    }

    @FXML
    void buscarMulta(ActionEvent event) {
        // a fazer
    }

}
