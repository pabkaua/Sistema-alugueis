package com.loja.controllers;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Map;

public class AlugueisClienteController {

    Cliente usuario;
    ILojaFacade facade;
    Map<String, ContratoAluguel> historico;

    public void setDados(Cliente usuario, ILojaFacade facade){
        this.usuario = usuario;
        this.facade = facade;
        this.historico = facade.consultarHistoricoCliente(usuario.getId());
    }
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