package com.loja;

import com.loja.controllers.Alerta;
import com.loja.controllers.LoginController;
import com.loja.padraofacade.LojaFacade;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Aplicacao extends Application {

    private ILojaFacade facade;

    public static void executar(String[] args){
        launch(args);
    }

    protected ILojaFacade criarFacade(){
        ILojaFacade facade = new LojaFacade();
        this.facade = facade;
        return facade;
    }

    @Override
    public void start(Stage stage) {
        try {
            URL url = getClass().getResource("/ui/LoginScreen.fxml");
            if (url == null) {
                Alerta.exibirAlertaAviso("Não foi possível carregar a tela de login!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent telaLogin = loader.load();

            LoginController controller = loader.getController();
            controller.setFacade(criarFacade());

            stage.setTitle("Login - Aluga Tudo");
            stage.setScene(new Scene(telaLogin, 1920, 1080));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            Alerta.exibirAlertaAviso("Erro ao carregar a interface.");
        }
    }

    @Override
    public void stop() {
        try {
            if (facade != null) {
                facade.salvarTudo();
            }
            super.stop();
        } catch (Exception e) {
            Alerta.exibirAlertaErro("ERRO GRAVE", "Erro ao salvar dados no encerramento: " + e.getMessage());
        }
    }
}