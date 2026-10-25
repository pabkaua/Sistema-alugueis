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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    private ILojaFacade facade;

    @FXML private Button botaoEntrar;
    @FXML private TextField inputLogin;
    @FXML private PasswordField inputSenha;

    public void initialize(){
        botaoEntrar.setOnAction(this::validarDados);
        botaoEntrar.setDefaultButton(true);
    }

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
    }

    @FXML
    void validarDados(ActionEvent event) {
        String email = inputLogin.getText().trim();
        String senha = inputSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            Alerta.exibirAlertaAviso("Preencha o e-mail e a senha.");
            return;
        }

        try {
            Usuario usuario = facade.autenticarUsuario(email, senha);

            if (usuario == null) {
                Alerta.exibirAlertaAviso("E-mail ou senha incorretos.");
                inputSenha.clear();
                return;
            }

            redirecionar(usuario, event);

        } catch (RuntimeException e) {
            Alerta.exibirAlertaAviso("Erro ao autenticar: " + e.getMessage());
        }
    }

    private void redirecionar(Usuario usuario, ActionEvent event) {
        if (usuario instanceof Cliente cliente) {
            FXMLLoader loader = abrirTela("/ui/cliente/sidebar-cliente.fxml", "Área do Cliente");
            if(loader == null){
                Alerta.exibirAlertaAviso("Não foi possivel logar, houve um erro no redirecionamento da tela");
                return;
            }
            SidebarClienteController controller = loader.getController();
            controller.setContexto(facade, cliente);
            controller.navegarDashboard(event);
        }
        else if (usuario instanceof Administrador adm){
            FXMLLoader loader = abrirTela("/ui/admin/sidebar-admin.fxml", "Painel do Administrador");
            if(loader == null){
                Alerta.exibirAlertaAviso("Não foi possivel logar, houve um erro no redirecionamento da tela");
                return;
            }
            SidebarAdminController controller = loader.getController();
            controller.setContexto(facade, adm);
            controller.navegarDashboard(event);
        }
        else if (usuario instanceof Funcionario func) {
            FXMLLoader loader = abrirTela("/ui/funcionario/sidebar-funcionario.fxml", "Painel do Funcionário");
            if(loader == null){
                Alerta.exibirAlertaAviso("Não foi possivel logar, houve um erro no redirecionamento da tela");
                return;
            }
            SidebarFuncionarioController controller = loader.getController();
            controller.setContexto(facade, func);
            controller.navegarDashboard(event);
        }
    }

    private FXMLLoader abrirTela(String caminhoFxml, String titulo) {
        try {
            URL url = getClass().getResource(caminhoFxml);
            if (url == null) {
                Alerta.exibirAlertaAviso("Arquivo não encontrado: " + caminhoFxml);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent tela = loader.load();

            Stage stage = (Stage) botaoEntrar.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(tela));
            stage.centerOnScreen();

            return loader;
        } catch (IOException e) {
            Alerta.exibirAlertaAviso("Erro ao carregar a interface.");
            return null;
        }
    }
}