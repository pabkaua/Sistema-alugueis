package com.loja;

import com.loja.business.*;
import com.loja.business.interfaces.*;
import com.loja.controllers.LoginController;
import com.loja.padraofacade.LojaFacade;
import com.loja.padraofacade.interfaces.ILojaFacade;
import com.loja.repositories.*;
import com.loja.repositories.interfaces.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Aplicacao extends Application {

    public static void executar(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            URL url = getClass().getResource("/ui/LoginScreen.fxml");
            if (url == null) {
                exibirAlerta("Não foi possível carregar a tela de login!");
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent telaLogin = loader.load();

            stage.setTitle("A Loja que aluga tudo");
            stage.setScene(new Scene(telaLogin, 1920, 1080));
            stage.show();

            LoginController controller = loader.getController();
            controller.setFacade(criarFacade());

        } catch (IOException e) {
            // debug e.printStackTrace();
            exibirAlerta("Erro ao carregar a interface.");
        }
    }

    protected ILojaFacade criarFacade(){
        // facade
        return new LojaFacade();
    }

    private void exibirAlerta(String msg) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}