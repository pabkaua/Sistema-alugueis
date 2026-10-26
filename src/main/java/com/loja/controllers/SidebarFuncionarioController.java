package com.loja.controllers;

import java.io.IOException;
import java.net.URL;

import com.loja.model.Funcionario;
import com.loja.padraofacade.interfaces.ILojaFacade;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    @FXML
    void navegarDashboard(ActionEvent event) {

    }

    @FXML
    void navegarAlugueis(ActionEvent event) {

    }

    @FXML
    void navegarMultas(ActionEvent event) {

    }

    @FXML
    void navegarCadastrarCliente(ActionEvent event) {

    }

    @FXML
    void navegarItensDisponiveis(ActionEvent event) {
        FXMLLoader loader = carregarTela("/ui/funcionario/itens-disponiveis.fxml");

        if (loader != null) {
            ItensDisponiveisController controller = loader.getController();
            controller.setFacade(facade);
            controller.carregarItens();
        }
    }

    @FXML
    void navegarContratosAtivos(ActionEvent event) {

    }

    @FXML
    void navegarContratosPorCliente(ActionEvent event) {
        FXMLLoader loader = carregarTela("/ui/funcionario/contratos-por-cliente.fxml");

        if (loader != null) {
            ContratosPorClienteController controller = loader.getController();
            controller.setFacade(facade);
        }
    }

    @FXML
    void fazerLogout(ActionEvent event) {


    }

    private void alternarSubmenu(VBox submenu) {
        boolean mostrar = !submenu.isVisible();
        submenu.setVisible(mostrar);
        submenu.setManaged(mostrar);
    }

    private FXMLLoader carregarTela(String caminho) {
        URL url = getClass().getResource(caminho);

        if (url == null) {
            System.out.println("Tela não encontrada: " + caminho);
            return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent tela = loader.load();
            conteudo.getChildren().setAll(tela);
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}