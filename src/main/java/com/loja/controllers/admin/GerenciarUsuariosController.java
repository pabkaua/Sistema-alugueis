package com.loja.controllers.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.loja.padraofacade.interfaces.ILojaFacade;
import com.loja.model.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class GerenciarUsuariosController {
    // Funcao de Listar
    @FXML private TableView<Usuario> adminTabelaUsuarios;
    @FXML private TableColumn<Usuario, String> adminColunaId;
    @FXML private TableColumn<Usuario, String> adminColunaNome;
    @FXML private TableColumn<Usuario, String> adminColunaLogin;
    @FXML private TableColumn<Usuario, String> adminColunaPerfil;
    @FXML private TableColumn<Usuario, String> adminColunaSituacao;

    // Funcoes de buscar, cadastrar, editar e desativar usuarios
    @FXML private TextField campoBuscaAdminUsuario;
    @FXML private Button btnAdminAdicionarUsuario;
    @FXML private Button btnAdminEditarUsuario;
    @FXML private Button btnAdminDesativarUsuario;
    private ILojaFacade facade;
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        adminColunaId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        adminColunaNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        adminColunaLogin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLogin()));
        adminColunaPerfil.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPerfil()));

        adminColunaSituacao.setCellValueFactory(c -> {
            return new SimpleStringProperty(c.getValue().isAtivo() ? "Ativo" : "Inativo");
        });

        btnAdminEditarUsuario.disableProperty().bind(adminTabelaUsuarios.getSelectionModel().selectedItemProperty().isNull());
        btnAdminDesativarUsuario.disableProperty().bind(adminTabelaUsuarios.getSelectionModel().selectedItemProperty().isNull());

        adminTabelaUsuarios.setPlaceholder(new Label("Nenhum usuário encontrado."));
        filtroDeBusca();

    }

    private void carregarDados(){
        if(facade != null && facade.listarUsuario() != null){
            listaUsuarios.setAll(facade.listarUsuario().values());
        }
    }
    public void setContexto(ILojaFacade facade){
        this.facade = facade;
        carregarDados();
    }

    private void filtroDeBusca(){
        FilteredList<Usuario> listaFiltrada = new FilteredList<>(listaUsuarios, u -> true);
        campoBuscaAdminUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(usuario -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String termo = newValue.toLowerCase();
                return usuario.getNome().toLowerCase().contains(termo)
                        || usuario.getLogin().toLowerCase().contains(termo)
                        || usuario.getId().toLowerCase().contains(termo);
            });
        });

        SortedList<Usuario> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(adminTabelaUsuarios.comparatorProperty());
        adminTabelaUsuarios.setItems(listaOrdenada);
    }

    @FXML
    void acaoDesativarUsuario(ActionEvent event){
        Usuario selecionado = adminTabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Desativação");
        alerta.setHeaderText("Desativar o usuário: " + selecionado.getNome());
        alerta.setContentText("Tem certeza? O histórico de aluguéis dele será mantido no sistema.");

        alerta.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                facade.desativarUsuario(selecionado.getId());
                carregarDados(); // Atualiza a tabela para mostrar o status Inativo
            }
        });

    }

    private void abrirFormulario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/admin/form-usuarios.fxml"));
            Parent raiz = loader.load();

            FormUsuarioController controller = loader.getController();
            controller.setContexto(this.facade, usuario);

            Stage janela = new Stage();
            janela.initModality(Modality.APPLICATION_MODAL);
            janela.initOwner(adminTabelaUsuarios.getScene().getWindow());
            janela.setScene(new Scene(raiz));
            janela.setResizable(false);
            janela.showAndWait(); // Trava e espera o popup fechar

            carregarDados();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir o formulário.").show();
        }
    }
    @FXML
    void acaoAdicionarUsuario(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML
    void acaoEditarUsuario(ActionEvent event) {
        Usuario selecionado = adminTabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            abrirFormulario(selecionado);
        }
    }
}
