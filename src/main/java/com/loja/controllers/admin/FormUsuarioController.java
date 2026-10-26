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

    }
}
