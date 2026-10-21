package com.loja.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class DashboardClienteController {

    @FXML private Label labelBoasVindas;

    private void exibirAlertaErro(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void carregarTela(MouseEvent event, String caminhoFXML) {
        try {
            URL url = getClass().getResource(caminhoFXML);
            if (url == null) {
                exibirAlertaErro("Arquivo não encontrado", "O arquivo '" + caminhoFXML + "' não foi localizado.");
                return;
            }

            Node novaTela = FXMLLoader.load(url);

            Node origem = (Node) event.getSource();
            StackPane conteudo = (StackPane) origem.getScene().lookup("#conteudo");

            if (conteudo != null) {
                conteudo.getChildren().setAll(novaTela);
            } else {
                exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela.");
            }

        } catch (IOException e) {
            exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela.");
        }
    }

    @FXML
    void navegarAlugueis(MouseEvent event) {
        carregarTela(event, "/ui/cliente/alugueis-cliente.fxml");
    }

    @FXML
    void navegarItensDisponiveis(MouseEvent event) {
        carregarTela(event, "/ui/cliente/disponiveis-cliente.fxml");
    }

    @FXML
    void navegarMultas(MouseEvent event) {
        carregarTela(event, "/ui/cliente/multas-cliente.fxml");
    }

}
