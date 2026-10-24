package com.loja.controllers;

import com.loja.model.Cliente;
import com.loja.padraofacade.LojaFacade;
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

    private void carregarTela(String caminhoFXML) {
        try {
            URL url = getClass().getResource(caminhoFXML);
            if (url == null) {
                exibirAlertaErro("Arquivo não encontrado", "O arquivo '" + caminhoFXML + "' não foi localizado.");
                return;
            }

            Node novaTela = FXMLLoader.load(url);
            this.conteudo.getChildren().setAll(novaTela);

        } catch (IOException e) {
            exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela.");
        }
    }

    private void exibirAlertaErro(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
    // ====================================================================================

    // funções do fxml
    @FXML
    public void initialize() {
        submenuPessoal.managedProperty().bind(submenuPessoal.visibleProperty());
        submenuRelatorios.managedProperty().bind(submenuRelatorios.visibleProperty());

        carregarTela("/ui/cliente/dashboard-cliente.fxml");
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
        carregarTela("/ui/cliente/dashboard-cliente.fxml");
    }

    @FXML
    void navegarAlugueis(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/cliente/alugueis-cliente.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            AlugueisClienteController controller = loader.getController();
            controller.setDados(usuario, facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            exibirAlertaErro("Erro de navegação", e.getMessage());
            // debug e.printStackTrace();
        }
    }

    @FXML
    void navegarMultas(ActionEvent event) {
        carregarTela("/ui/cliente/multas-cliente.fxml");
    }

    @FXML
    void navegarItensDisponiveis(ActionEvent event) {
        carregarTela("/ui/cliente/disponiveis-cliente.fxml");
    }

    @FXML
    void fazerLogout(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/LoginScreen.fxml");
            if (url == null) {
                exibirAlertaErro("Não foi possivel fazer o logout", "O arquivo LoginScreen.fxml não foi localizado.");
                return;
            }

            Parent telaLogin = FXMLLoader.load(url);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // pra ele saber em qual tela tá

            Scene loginScene = new Scene(telaLogin);
            stage.setScene(loginScene);

        } catch (IOException e) {
            exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela.");
        }
    }
    // ====================================================================================
}
