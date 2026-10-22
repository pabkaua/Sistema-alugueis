package com.loja;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Aplicacao extends Application {

    public static void executar(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        Parent telaLogin = FXMLLoader.load(getClass().getResource("/ui/LoginScreen.fxml"));

        stage.setTitle("A Loja que aluga tudo");
        stage.setScene(new Scene(telaLogin, 1920, 1080));
        stage.show();
    }
}