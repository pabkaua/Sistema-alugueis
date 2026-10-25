package com.loja.controllers;

import com.loja.model.Funcionario;
import com.loja.padraofacade.interfaces.ILojaFacade;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SidebarFuncionarioController {

    private ILojaFacade facade;
    private Funcionario funcionarioLogado;

    @FXML private Button botaoDashboard;
    @FXML private Button botaoLogout;
    @FXML private Button btnFuncAlugueis;
    @FXML private Button btnFuncCadastrarCliente;
    @FXML private Button btnFuncContratosAtivos;
    @FXML private Button btnFuncContratosPorClientes;
    @FXML private Button btnFuncGerenciarClientes;
    @FXML private Button btnFuncItensDisponiveis;
    @FXML private Button btnFuncQuitarMulta;
    @FXML private Button btnFuncRegistrarAluguel;
    @FXML private Button btnFuncRelatorios;
    @FXML private StackPane conteudo;
    @FXML private VBox sidebarVbox;
    @FXML private Label tituloLoja;
    @FXML private VBox submenuAlugueisFunc;
    @FXML private VBox submenuGerenciarClientesFunc;
    @FXML private VBox submenuRelatoriosFunc;

    @FXML
    void initialize() {
        submenuAlugueisFunc.setManaged(false);
        submenuGerenciarClientesFunc.setManaged(false);
        submenuRelatoriosFunc.setManaged(false);
    }

    public void setContexto(ILojaFacade facade, Funcionario funcionario) {
        this.facade = facade;
        this.funcionarioLogado = funcionario;
    }

    @FXML
    void alternarAlugueis(ActionEvent event) {
        alternarSubmenu(submenuAlugueisFunc);
    }

    @FXML
    void alternarGerenciarClientes(ActionEvent event) {
        alternarSubmenu(submenuGerenciarClientesFunc);
    }

    @FXML
    void alternarRelatorios(ActionEvent event) {
        alternarSubmenu(submenuRelatoriosFunc);
    }

    private void alternarSubmenu(VBox submenu) {
        boolean mostrar = !submenu.isVisible();
        submenu.setVisible(mostrar);
        submenu.setManaged(mostrar);
    }

    @FXML
    void fazerLogout(ActionEvent event) {

    }

    @FXML
    void navegarDashboard(ActionEvent event) {

    }

    @FXML
    void navegarRegistrarAluguel(ActionEvent event) {

    }

    @FXML
    void navegarQuitarMulta(ActionEvent event) {

    }

    @FXML
    void navegarCadastrarCliente(ActionEvent event) {

    }

    @FXML
    void navegarItensDisponiveis(ActionEvent event) {

    }

    @FXML
    void navegarContratosAtivos(ActionEvent event) {

    }

    @FXML
    void navegarContratosPorCliente(ActionEvent event) {

    }
}