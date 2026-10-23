package com.loja.controllers;

import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private ILojaFacade facade;

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
    }

    @FXML
    private Button botaoEntrar;

    @FXML
    private TextField inputLogin;

    @FXML
    private PasswordField inputSenha;

    @FXML
    void validarDados(ActionEvent event) {
        String email = inputLogin.getText().trim();
        String senha = inputSenha.getText();

        if (email.isEmpty() || senha.isEmpty()){

        }

        private void exibirAlerta(String mensagem) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setHeaderText(null);
            alerta.setContentText(mensagem);
            alerta.showAndWait();
        }
    }

}
