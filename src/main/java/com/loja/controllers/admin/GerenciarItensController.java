package com.loja.controllers.admin;

import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class GerenciarItensController {

    ILojaFacade facade;

    @FXML private TableColumn<?, ?> adminColunaCategoria;
    @FXML private TableColumn<?, ?> adminColunaFornecedor;
    @FXML private TableColumn<?, ?> adminColunaId;
    @FXML private TableColumn<?, ?> adminColunaNome;
    @FXML private TableColumn<?, ?> adminColunaStatus;
    @FXML private TableColumn<?, ?> adminColunaValorDiario;
    @FXML private TableColumn<?, ?> adminColunaValorRepo;
    @FXML private TableView<?> adminTabelaItens;
    @FXML private Button btnAdminDeletar;
    @FXML private Button btnAdminEditar;
    @FXML private Button btnAdminNovo;
    @FXML private TextField campoBuscaItem;

    public void setContexto(ILojaFacade facade){
        this.facade = facade;
    }

    @FXML
    void acaoAdicionar(ActionEvent event) {

    }

    @FXML
    void acaoDeletar(ActionEvent event) {

    }

    @FXML
    void acaoEditar(ActionEvent event) {

    }

}
