package com.loja.controllers.cliente;

import com.loja.controllers.Alerta;
import com.loja.model.Cliente;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class DashboardClienteController {

    @FXML private Label labelBoasVindas;
    private ILojaFacade facade;
    private Cliente usuario;
    private Pane conteudo;

    public void setDados(Cliente usuario, ILojaFacade facade, Pane conteudo){
        labelBoasVindas.setText("Olá, " + usuario.getNome() + "!");
        this.usuario = usuario;
        this.facade = facade;
        this.conteudo = conteudo;
    }

    @FXML
    void navegarAlugueis() {
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
    void navegarItensDisponiveis() {
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
    void navegarMultas() {
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
}
