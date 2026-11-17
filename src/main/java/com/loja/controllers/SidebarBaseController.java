package com.loja.controllers;

import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public abstract class SidebarBaseController {

    private ILojaFacade facade;

    @FXML private Label tituloLoja;
    @FXML private Button botaoDashboard;
    @FXML private VBox sidebarVbox;
    @FXML private Button botaoLogout;

    public void setFacade(ILojaFacade facade){
        this.facade = facade;
    }

    public ILojaFacade getFacade() {
        return facade;
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

            stage.setTitle("Login - Aluga Tudo");

            if (stage.getScene() != null) {
                stage.getScene().setRoot(telaLogin);
            } else {
                stage.setScene(new Scene(telaLogin));
            }

        } catch (IOException e) {
            Alerta.exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela de login.");
        }
    }
}
