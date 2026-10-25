package com.loja.controllers;

import com.loja.model.Cliente;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SidebarClienteController {

    @FXML private Button botaoAlugueis;
    @FXML private Button botaoDashboard;
    @FXML private Button botaoItensDisponiveis;
    @FXML private Button botaoLogout;
    @FXML private Button botaoMultas;
    @FXML private Button botaoPessoal;
    @FXML private Button botaoRelatorios;
    @FXML private StackPane conteudo;
    @FXML private VBox sidebarVbox;
    @FXML private VBox submenuPessoal;
    @FXML private VBox submenuRelatorios;
    @FXML private Label tituloLoja;

    private Cliente usuario;
    private ILojaFacade facade;

    // funções da classe
    public void setContexto(ILojaFacade facade, Cliente cliente){
        this.usuario = cliente;
        this.facade = facade;
    }

    // ====================================================================================

    // funções do fxml
    @FXML
    public void initialize() {
        submenuPessoal.managedProperty().bind(submenuPessoal.visibleProperty());
        submenuRelatorios.managedProperty().bind(submenuRelatorios.visibleProperty());
    }

    @FXML
    void alternarPessoal(ActionEvent event) {
        boolean visivel = !submenuPessoal.isVisible();
        submenuPessoal.setVisible(visivel);
        botaoPessoal.setText(visivel ? "⌃   Pessoal" : "⌄   Pessoal");
    }

    @FXML
    void alternarRelatorios(ActionEvent event) {
        boolean visivel = !submenuRelatorios.isVisible();
        submenuRelatorios.setVisible(visivel);
        botaoRelatorios.setText(visivel ? "⌃   Relatórios" : "⌄   Relatórios");
    }
    // ====================================================================================

    // funções de navegação
    @FXML
    void navegarDashboard(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/cliente/dashboard-cliente.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo dashboard-cliente.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            DashboardClienteController controller = loader.getController();
            controller.setUsuario(usuario);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void navegarAlugueis(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/cliente/alugueis-cliente.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo alugueis-cliente.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            AlugueisClienteController controller = loader.getController();
            controller.setDados(usuario, facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void navegarMultas(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/cliente/multas-cliente.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo multas-cliente.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            MultasClienteController controller = loader.getController();
            controller.setDados(usuario, facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void navegarItensDisponiveis(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/cliente/disponiveis-cliente.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo disponiveis-cliente.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            DisponiveisClienteController controller = loader.getController();
            controller.setDados(facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void fazerLogout(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/LoginScreen.fxml");
            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo LoginScreen.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent telaLogin = loader.load();

            LoginController controller = loader.getController();
            controller.setFacade(this.facade);

            Node botaoLogoutClicado = (Node) event.getSource();
            Stage stage = (Stage) botaoLogoutClicado.getScene().getWindow();

            Scene loginScene = new Scene(telaLogin);
            stage.setScene(loginScene);
            stage.setTitle("Login - Aluga Tudo");
            stage.centerOnScreen();

        } catch (IOException e) {
            Alerta.exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela.");
        }
    }
    // ====================================================================================
}