package com.loja.controllers;

import com.loja.model.Cliente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class DashboardClienteController {

    @FXML private Label labelBoasVindas;

    public void setUsuario(Cliente usuario){
        labelBoasVindas.setText("Olá, " + usuario.getNome() + "!");
    }

    private void carregarTela(MouseEvent event, String caminhoFXML) {
        try {
            URL url = getClass().getResource(caminhoFXML);
            if (url == null) {
                Alerta.exibirAlertaErro("Arquivo não encontrado", "O arquivo '" + caminhoFXML + "' não foi localizado.");
                return;
            }

            Node novaTela = FXMLLoader.load(url);

            Node origem = (Node) event.getSource();
            StackPane conteudo = (StackPane) origem.getScene().lookup("#conteudo");

            if (conteudo != null) {
                conteudo.getChildren().setAll(novaTela);
            } else {
                Alerta.exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela.");
            }

        } catch (IOException e) {
            Alerta.exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela.");
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
