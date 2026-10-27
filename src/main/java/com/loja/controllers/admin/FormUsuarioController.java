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

    @FXML
    void acaoSalvar(ActionEvent event) {
        esconderErros();
        boolean valido = true;

        // Validação usando &= para mostrar todos os erros de uma vez
        if (campoNome.getText() == null || campoNome.getText().trim().isEmpty()) {
            erroNome.setText("O nome é obrigatório.");
            erroNome.setVisible(true);
            valido &= false;
        }

        if (campoLogin.getText() == null || campoLogin.getText().trim().isEmpty()) {
            erroLogin.setText("O login é obrigatório.");
            erroLogin.setVisible(true);
            valido &= false;
        }

        if (campoPerfil.getValue() == null) {
            erroPerfil.setText("Selecione um perfil.");
            erroPerfil.setVisible(true);
            valido &= false;
        }

        String senha = campoSenha.getText();
        String confirmar = campoConfirmarSenha.getText();

        // Se for cadastro novo, a senha é obrigatória. Na edição, só valida se o utilizador digitar algo.
        if (usuarioEditando == null || (senha != null && !senha.isEmpty())) {
            if (senha == null || senha.trim().isEmpty()) {
                erroSenha.setText("A senha é obrigatória.");
                erroSenha.setVisible(true);
                valido &= false;
            } else if (!senha.equals(confirmar)) {
                erroConfirmarSenha.setText("As senhas não coincidem.");
                erroConfirmarSenha.setVisible(true);
                valido &= false;
            }
        }

        if (valido) {
            processarSalvamento();
            fecharJanela();
        }
    }

    private void processarSalvamento() {
        String nome = campoNome.getText().trim();
        String login = campoLogin.getText().trim();
        String senha = campoSenha.getText(); // Pode estar vazia na edição
        String perfil = campoPerfil.getValue();

        if (usuarioEditando == null) {
            // Lógica de CADASTRO
            if ("ADMINISTRADOR".equals(perfil)) {
                Administrador novoAdm = new Administrador(id, nome, login, senha);
                facade.cadastrarAdm(novoAdm);
            } else {
                Funcionario novoFunc = new Funcionario(id, nome, login, senha, "Atendente");
                facade.cadastrarFuncionario(novoFunc);
            }
            System.out.println("Usuário cadastrado com sucesso.");
        } else {
            // Lógica de EDIÇÃO
            usuarioEditando.setNome(nome);
            if (senha != null && !senha.isEmpty()) {
                usuarioEditando.setSenha(senha);
            }
            facade.atualizarUsuario(usuarioEditando.getId(), usuarioEditando);
            System.out.println("Usuário editado com sucesso.");
            facade.salvarTudo();
        }
    }

    @FXML
    void acaoCancelar(ActionEvent event) {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) campoNome.getScene().getWindow();
        stage.close();
    }
}
