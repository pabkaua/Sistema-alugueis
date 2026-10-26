package com.loja.controllers.admin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.loja.padraofacade.interfaces.ILojaFacade;
import com.loja.model.Usuario;
import com.loja.model.Administrador;
import com.loja.model.Funcionario;

public class FormUsuarioController {
    @FXML private Label lblTitulo;
    @FXML private TextField campoNome;
    @FXML private Label erroNome;
    @FXML private TextField campoLogin;
    @FXML private Label erroLogin;
    @FXML private ComboBox<String> campoPerfil;
    @FXML private Label erroPerfil;
    @FXML private PasswordField campoSenha;
    @FXML private Label erroSenha;
    @FXML private PasswordField campoConfirmarSenha;
    @FXML private Label erroConfirmarSenha;

    private ILojaFacade facade;
    private Usuario usuarioEditando;

    @FXML
    public void initialize(){
        campoPerfil.getItems().addAll("ADMINISTRADOR", "FUNCIONARIO", "CLIENTE");
        erroNome.managedProperty().bind(erroNome.visibleProperty());
        erroLogin.managedProperty().bind(erroLogin.visibleProperty());
        erroPerfil.managedProperty().bind(erroPerfil.visibleProperty());
        erroSenha.managedProperty().bind(erroSenha.visibleProperty());
        erroConfirmarSenha.managedProperty().bind(erroConfirmarSenha.visibleProperty());

        esconderErros();
        Platform.runLater(campoNome::requestFocus);

    }
    private void esconderErros() {
        erroNome.setVisible(false);
        erroLogin.setVisible(false);
        erroPerfil.setVisible(false);
        erroSenha.setVisible(false);
        erroConfirmarSenha.setVisible(false);
    }

    public void setContexto(ILojaFacade facade, Usuario usuario) {
        this.facade = facade;
        this.usuarioEditando = usuario;

        if (usuario != null) {
            lblTitulo.setText("Editar Usuário");
            campoNome.setText(usuario.getNome());
            campoLogin.setText(usuario.getLogin());
            campoLogin.setEditable(false);
            campoLogin.setDisable(true);
            campoPerfil.setValue(usuario.getPerfil().toUpperCase());
            campoPerfil.setDisable(true);
        }
    }
}
