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
import com.loja.model.Cliente;

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
    @FXML private ComboBox<String> campoSituacao;

    private ILojaFacade facade;
    private Usuario usuarioEditando;

    @FXML
    public void initialize(){
        campoPerfil.getItems().addAll("ADMINISTRADOR", "FUNCIONARIO", "CLIENTE");
        campoSituacao.getItems().addAll("ATIVO", "INATIVO");
        campoSituacao.setValue("ATIVO");
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
            campoSituacao.setValue(usuario.isAtivo() ? "ATIVO" : "INATIVO");
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
        String id = gerarNovoId();
        Boolean isAtivo = "ATIVO".equals(campoSituacao.getValue());

        if (usuarioEditando == null) {
            String novoId = gerarNovoId();

            if ("ADMINISTRADOR".equals(perfil)) {
                Administrador novoAdm = new Administrador(novoId, nome, login, senha);
                novoAdm.setAtivo(isAtivo); //[cite: 10]
                facade.cadastrarAdm(novoAdm);
            } else if ("FUNCIONARIO".equals(perfil)) {
                Funcionario novoFunc = new Funcionario(novoId, nome, login, senha, "Atendente");
                novoFunc.setAtivo(isAtivo); //[cite: 10]
                facade.cadastrarFuncionario(novoFunc);
            } else if ("CLIENTE".equals(perfil)) {
                Cliente novoCliente = new Cliente(novoId, nome, login, senha);
                novoCliente.setAtivo(isAtivo); //[cite: 10]
                facade.cadastrarCliente(novoCliente);
            }
        } else {
            usuarioEditando.setNome(nome);
            usuarioEditando.setAtivo(isAtivo); //[cite: 10]
            if (senha != null && !senha.isEmpty()) {
                usuarioEditando.setSenha(senha);
            }
            facade.atualizarUsuario(usuarioEditando.getId(), usuarioEditando);
        }
        facade.salvarTudo();
    }

    @FXML
    void acaoCancelar(ActionEvent event) {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) campoNome.getScene().getWindow();
        stage.close();
    }

    private String gerarNovoId(){
        int maxNumero = 0;
        for (Usuario u : facade.listarUsuario().values()){
            if(u.getId() != null && u.getId().toUpperCase().startsWith("USR")){
                try {
                    int numero = Integer.parseInt(u.getId().substring(3));
                    if (numero > maxNumero){
                        maxNumero = numero;
                    }
                } catch (NumberFormatException ignored){}
            }
        }
        return String.format("USR%03d", maxNumero + 1);
    }
}
