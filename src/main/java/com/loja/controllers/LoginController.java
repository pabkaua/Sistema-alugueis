package com.loja.controllers;

import com.loja.model.Administrador;
import com.loja.model.Cliente;
import com.loja.model.Funcionario;
import com.loja.model.Usuario;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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

        if (email.isEmpty() || senha.isEmpty()) {
            exibirAlerta("Preencha o e-mail e a senha.");
            return;
        }

        try {
            Usuario usuario = facade.autenticarUsuario(email, senha);

            if (usuario == null) {
                exibirAlerta("E-mail ou senha incorretos.");
                inputSenha.clear();
                return;
            }

            //redirecionar(usuario);

        } catch (RuntimeException e) {
            exibirAlerta("Erro ao autenticar: " + e.getMessage());
        }
    }



/*private void redirecionar(Usuario usuario)throws IOException {
    if (usuario instanceof Administrador adm) {
        FXMLLoader loader = abrirTela("/ui/admin/MenuAdmin.fxml", "Painel do Administrador");
        ((MenuAdminController) loader.getController()).setContexto(facade, adm);

    } else if (usuario instanceof Funcionario func) {
        FXMLLoader loader = abrirTela("/ui/funcionario/MenuFuncionario.fxml", "Painel do Funcionário");
        ((MenuFuncionarioController) loader.getController()).setContexto(facade, func);

    } else if (usuario instanceof Cliente cliente) {
        FXMLLoader loader = abrirTela("/ui/cliente/MenuCliente.fxml", "Área do Cliente");
        ((MenuClienteController) loader.getController()).setContexto(facade, cliente);
    }
}

private FXMLLoader abrirTela(String caminhoFxml, String titulo) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
    Parent root = loader.load();

    Stage stage = (Stage) botaoEntrar.getScene().getWindow();
    stage.setTitle(titulo);
    stage.setScene(new Scene(root));
    stage.centerOnScreen();

    return loader;
}*/

private void exibirAlerta(String msg) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}


