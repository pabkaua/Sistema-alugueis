package com.loja.controllers.funcionario;

import com.loja.controllers.Alerta;
import com.loja.model.Cliente;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastrarClienteFuncionarioController {

    private ILojaFacade facade;

    @FXML private Button botaoCadastrar;
    @FXML private Button botaoLimpar;
    @FXML private TextField campoID;
    @FXML private TextField campoNome;
    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private PasswordField campoConfirmarSenha;

    public void initialize() {
        campoID.setOnAction(e -> campoID.requestFocus());
        campoNome.setOnAction(e -> campoLogin.requestFocus());
        campoLogin.setOnAction(e -> campoSenha.requestFocus());
        campoSenha.setOnAction(e -> campoConfirmarSenha.requestFocus());
        campoConfirmarSenha.setOnAction(this::cadastrarCliente);
    }

    public void setDados(ILojaFacade facade) {
        this.facade = facade;
    }

    private void limparFormulario() {
        campoID.clear();
        campoNome.clear();
        campoLogin.clear();
        campoSenha.clear();
        campoConfirmarSenha.clear();
        campoID.requestFocus();
    }

    private void mostrarInfo(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    @FXML
    void cadastrarCliente(ActionEvent event) {
        String id = campoID.getText().trim();
        String nome = campoNome.getText().trim();
        String login = campoLogin.getText().trim();
        String senha = campoSenha.getText();
        String confirmacao = campoConfirmarSenha.getText();

        if (id.isEmpty() || nome.isEmpty() || login.isEmpty() || senha.isEmpty() || confirmacao.isEmpty()) {
            Alerta.exibirAlertaAviso("Preencha todos os campos para cadastrar o cliente.");
            return;
        }

        if (!senha.equals(confirmacao)) {
            Alerta.exibirAlertaAviso("As senhas não conferem.");
            campoConfirmarSenha.clear();
            campoConfirmarSenha.requestFocus();
            return;
        }

        try {
            Cliente cliente = new Cliente(id, nome, login, senha);
            cliente.setAtivo(true);

            facade.cadastrarCliente(cliente);
            mostrarInfo("Cliente cadastrado! ID: " + cliente.getId());
            limparFormulario();
        } catch (RuntimeException e) {
            Alerta.exibirAlertaErro("Erro ao cadastrar cliente", e.getMessage());
        }
    }

    @FXML
    void limpar(ActionEvent event) {
        limparFormulario();
    }
}