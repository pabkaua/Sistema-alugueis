package com.loja.controllers.admin;

import com.loja.controllers.Alerta;
import com.loja.model.Fornecedor;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GerenciarFornecedoresController {

    @FXML private Button botaoBuscar;
    @FXML private Button botaoNovoForne;
    @FXML private Button botaoEditar;
    @FXML private Button botaoExcluir;
    @FXML private TextField campoBusca;
    @FXML private TableColumn<Fornecedor, Void> colunaSelecionar;
    @FXML private TableColumn<Fornecedor, String> colunaID;
    @FXML private TableColumn<Fornecedor, String> colunaNome;
    @FXML private TableColumn<Fornecedor, String> colunaCNPJ;
    @FXML private TableColumn<Fornecedor, String> colunaTelefone;
    @FXML private ComboBox<String> listaColuna;
    @FXML private TableView<Fornecedor> tabela;

    private ILojaFacade facade;

    private final ObservableList<Fornecedor> dados = FXCollections.observableArrayList();
    private FilteredList<Fornecedor> filtrados;

    private final ObjectProperty<Fornecedor> selecionado = new SimpleObjectProperty<>();

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
        if (tabela != null) {
            recarregarTabela();
        }
    }

    @FXML
    public void initialize() {
        configurarColunas();
        configurarColunaSelecionar();
        configurarBusca();
        configurarBotoesDeAcao();
        recarregarTabela();
    }

    private void configurarColunas() {
        colunaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCNPJ.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        filtrados = new FilteredList<>(dados, f -> true);
        SortedList<Fornecedor> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(ordenados);
        tabela.setPlaceholder(new Label("Não há conteúdo na tabela"));
    }

    private void configurarColunaSelecionar() {
        colunaSelecionar.setSortable(false);
        colunaSelecionar.setCellFactory(coluna -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                setAlignment(Pos.CENTER);
                checkBox.setOnAction(e -> {
                    Fornecedor fornecedor = getTableRow().getItem();
                    selecionado.set(checkBox.isSelected() ? fornecedor : null);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Fornecedor fornecedor = getTableRow() == null ? null : getTableRow().getItem();
                if (empty || fornecedor == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(fornecedor == selecionado.get());
                    setGraphic(checkBox);
                }
            }
        });

        // Desmarca o anterior quando outro é marcado
        selecionado.addListener((obs, anterior, novo) -> tabela.refresh());
    }

    private void configurarBusca() {
        listaColuna.getItems().setAll("Todos", "ID", "Nome", "CNPJ", "Telefone");
        listaColuna.getSelectionModel().select("Todos");

        campoBusca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro());
        listaColuna.valueProperty().addListener((obs, antigo, novo) -> aplicarFiltro());
        campoBusca.setOnAction(e -> aplicarFiltro());
    }

    private void configurarBotoesDeAcao() {
        botaoEditar.setOnAction(this::editar);
        botaoExcluir.setOnAction(this::excluir);

        var mostrar = selecionado.isNotNull();

        botaoEditar.visibleProperty().bind(mostrar);
        botaoEditar.managedProperty().bind(mostrar);
        botaoExcluir.visibleProperty().bind(mostrar);
        botaoExcluir.managedProperty().bind(mostrar);
    }

    private void aplicarFiltro() {
        String texto = campoBusca.getText() == null ? "" : campoBusca.getText().toLowerCase().trim();
        String coluna = listaColuna.getValue() == null ? "Todos" : listaColuna.getValue();

        filtrados.setPredicate(f -> {
            if (texto.isEmpty()) return true;
            return switch (coluna) {
                case "ID" -> contem(f.getId(), texto);
                case "Nome" -> contem(f.getNome(), texto);
                case "CNPJ" -> contem(f.getCnpj(), texto);
                case "Telefone" -> contem(f.getTelefone(), texto);
                default -> contem(f.getId(), texto) || contem(f.getNome(), texto)
                        || contem(f.getCnpj(), texto) || contem(f.getTelefone(), texto);
            };
        });
    }

    private boolean contem(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    @FXML
    void buscar(ActionEvent event) {
        aplicarFiltro();
    }

    @FXML
    void novoFornecedor(ActionEvent event) {
        selecionado.set(null);
        abrirFormulario(null);
    }

    @FXML
    void editar(ActionEvent event) {
        Fornecedor fornecedor = selecionado.get();
        if (fornecedor == null) return;

        abrirFormulario(fornecedor);
        selecionado.set(null);
    }

    @FXML
    void excluir(ActionEvent event) {
        Fornecedor fornecedor = selecionado.get();
        if (fornecedor == null) return;

        boolean confirmou = Alerta.confirmar("Excluir fornecedor",
                "Deseja excluir o fornecedor \"" + fornecedor.getNome() + "\" (ID " + fornecedor.getId() + ")?");

        if (confirmou) {
            try {
                facade.deletarFornecedor(fornecedor.getId());
                recarregarTabela();
                Alerta.exibirAlertaSucesso("Fornecedor removido.");
            } catch (RuntimeException e) {
                Alerta.exibirAlertaErro("Erro ao excluir fornecedor", e.getMessage());
            }
        }

        selecionado.set(null);
    }

    private void abrirFormulario(Fornecedor fornecedor) {
        try {
            java.net.URL url = getClass().getResource("/ui/admin/formFornecedor-admin.fxml");
            if (url == null) {
                Alerta.exibirAlertaErro("Erro ao abrir formulário",
                        "Arquivo não encontrado: /ui/admin/formFornecedor-admin.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent raiz = loader.load();

            NvForneAdminController controller = loader.getController();
            controller.setFacade(facade);
            controller.setFornecedor(fornecedor);

            Stage stage = new Stage();
            stage.setTitle(fornecedor == null ? "Novo fornecedor" : "Editar fornecedor");
            stage.setScene(new Scene(raiz));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tabela.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

            recarregarTabela();

        } catch (Exception e) {
            Throwable causa = e;
            while (causa.getCause() != null) causa = causa.getCause();
            Alerta.exibirAlertaErro("Erro ao abrir formulário", causa.toString());
        }
    }

    private void recarregarTabela() {
        if (facade == null) return;
        dados.setAll(facade.listarFornecedor().values());
        tabela.refresh();
    }
}