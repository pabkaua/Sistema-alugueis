package com.loja.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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

    @FXML
    public void initialize() {
        // esse .bind vai meio que cruzar as propriedades, fazendo uma se espelhar na outra
        // managed serve pra dizer se o componente conta ou não no calculo dos tamanho
        submenuPessoal.managedProperty().bind(submenuPessoal.visibleProperty());
        submenuRelatorios.managedProperty().bind(submenuRelatorios.visibleProperty());

        //carregarTela("/ui/cliente/dashboard-cliente.fxml");
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

    @FXML
    void fazerLogout(ActionEvent event) {

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

    @FXML
    void navegarDashboard(ActionEvent event) {
        carregarTela("/ui/cliente/dashboard-cliente.fxml");
    }

    @FXML
    void navegarAlugueis(ActionEvent event) {
        carregarTela("/ui/cliente/alugueis-cliente.fxml");
    }

    @FXML
    void navegarMultas(ActionEvent event) {
        carregarTela("/ui/cliente/multas-cliente.fxml");
    }

    @FXML
    void navegarItensDisponiveis(ActionEvent event) {
        carregarTela("/ui/cliente/disponiveis-cliente.fxml");
    }

}
