package com.loja.controllers.admin;

import com.loja.model.Categoria;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

public class CategAdminController {

    @FXML
    private TextField IdCategoria;

    @FXML
    private TextField NomeCategoria;

    @FXML
    private Button botaoBuscar;

    @FXML
    private Button botaoCateAdd;

    @FXML
    private Button botaoEditar;

    @FXML
    private Button botaoExcluir;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<Categoria, String> colunaID;

    @FXML
    private TableColumn<Categoria, String> colunaNome;

    @FXML
    private TableColumn<Categoria, String> colunaSelecionar;

    @FXML
    private ComboBox<String> listaColuna;

    @FXML
    private TableView<Categoria> tabela;

    private ILojaFacade facade;

    private final ObservableList<Categoria> dados = FXCollections.observableArrayList();
    private FilteredList<Categoria> filtrados;

    private final ObjectProperty<Categoria> selecionada = new SimpleObjectProperty<>();

    private final BooleanProperty modoEdicao = new SimpleBooleanProperty(false);
    private Categoria emEdicao;

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
        //recarregarTabela();
    }

    private void configurarColunas() {
        colunaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        filtrados = new FilteredList<>(dados, c -> true);
        tabela.setItems(filtrados);
        tabela.setPlaceholder(new javafx.scene.control.Label("Não há conteúdo na tabela"));
    }

    private void configurarColunaSelecionar() {
        colunaSelecionar.setSortable(false);
        colunaSelecionar.setCellFactory(coluna -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                setAlignment(Pos.CENTER);
                checkBox.setOnAction(e -> {
                    Categoria categoria = getTableRow().getItem();
                    selecionada.set(checkBox.isSelected() ? categoria : null);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Categoria categoria = getTableRow() == null ? null : getTableRow().getItem();
                if (empty || categoria == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(categoria == selecionada.get());
                    setGraphic(checkBox);
                }
            }
        });

        selecionada.addListener((obs, anterior, nova) -> {
            if (modoEdicao.get() && nova != emEdicao) {
                limparFormulario();
            }
            tabela.refresh();
        });
    }

    private void configurarBusca() {
        listaColuna.getItems().setAll("Todos", "ID", "Nome");
        listaColuna.getSelectionModel().select("Todos");

        campoBusca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro());
        listaColuna.valueProperty().addListener((obs, antigo, novo) -> aplicarFiltro());
        campoBusca.setOnAction(e -> aplicarFiltro());
    }

    private void configurarBotoesDeAcao() {
        // Editar e Excluir só aparecem com uma categoria marcada e fora da edição
        var mostrar = selecionada.isNotNull().and(modoEdicao.not());

        botaoEditar.visibleProperty().bind(mostrar);
        botaoEditar.managedProperty().bind(mostrar);
        botaoExcluir.visibleProperty().bind(mostrar);
        botaoExcluir.managedProperty().bind(mostrar);
    }

    private void configurarTeclado() {
        // Enter nos campos confirma (cadastra ou salva a edição)
        NomeCategoria.setOnAction(e -> confirmar());
        IdCategoria.setOnAction(e -> confirmar());

        // Esc cancela a edição
        NomeCategoria.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE && modoEdicao.get()) {
                voltarAoEstadoInicial();
            }
        });
    }


    private void confirmar() {
        if (modoEdicao.get()) {
            salvarEdicao();
        } else {
            cadastrar();
        }
    }

    private void cadastrar() {
        String id = IdCategoria.getText().trim();
        String nome = NomeCategoria.getText().trim();

        if (nome.isEmpty()) {
            mostrarErro("O nome da categoria não pode ficar vazio.");
            NomeCategoria.requestFocus();
            return;
        }

        try {
            facade.cadastrarCategoria(new Categoria(id, nome));
            recarregarTabela();
            mostrarInfo("Categoria criada!");
            voltarAoEstadoInicial();
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void salvarEdicao() {
        String novoNome = NomeCategoria.getText().trim();

        if (novoNome.isEmpty()) {
            mostrarErro("O nome da categoria não pode ficar vazio.");
            NomeCategoria.requestFocus();
            return;
        }

        String nomeAntigo = emEdicao.getNome();
        try {
            emEdicao.setNome(novoNome);
            facade.atualizarCategoria(emEdicao);
            recarregarTabela();
            mostrarInfo("Categoria atualizada!");
            voltarAoEstadoInicial();
        } catch (RuntimeException e) {
            emEdicao.setNome(nomeAntigo);
            mostrarErro(e.getMessage());
        }
    }

    private void aplicarFiltro() {
        String texto = campoBusca.getText() == null ? "" : campoBusca.getText().toLowerCase().trim();
        String coluna = listaColuna.getValue() == null ? "Todos" : listaColuna.getValue();

        filtrados.setPredicate(c -> {
            if (texto.isEmpty()) return true;
            return switch (coluna) {
                case "ID" -> contem(c.getId(), texto);
                case "Nome" -> contem(c.getNome(), texto);
                default -> contem(c.getId(), texto) || contem(c.getNome(), texto);
            };
        });
    }

    private boolean contem(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    private void recarregarTabela() {
        if (facade == null) return;
        dados.setAll(facade.listarCategoria().values());
        tabela.refresh();
    }

    private void limparFormulario() {
        emEdicao = null;
        modoEdicao.set(false);
        IdCategoria.clear();
        IdCategoria.setDisable(false);
        NomeCategoria.clear();
        botaoCateAdd.setText("+ Adicionar");
    }

    private void voltarAoEstadoInicial() {
        limparFormulario();
        selecionada.set(null);
        IdCategoria.requestFocus();
    }

    private void mostrarInfo(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }



    @FXML
    void buscar(ActionEvent event) {

    }

}