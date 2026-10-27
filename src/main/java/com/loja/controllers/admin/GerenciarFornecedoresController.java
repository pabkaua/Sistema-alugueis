package com.loja.controllers.admin;

import com.loja.controllers.Alerta;
import com.loja.model.Fornecedor;
import com.loja.padraofacade.interfaces.ILojaFacade;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GerenciarFornecedoresController {

    @FXML
    private Button botaoBuscar11;

    @FXML
    private Button botaoNovoForne;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<Fornecedor, String> colunaCNPJ;

    @FXML
    private TableColumn<Fornecedor, String> colunaID;

    @FXML
    private TableColumn<Fornecedor, String>colunaNome;

    @FXML
    private TableColumn<Fornecedor, String> colunaTelefone;

    @FXML
    private ComboBox<String> listaColuna;

    @FXML
    private TableView<Fornecedor> tabela;

    private ILojaFacade facade;

    private final ObservableList<Fornecedor> dados = FXCollections.observableArrayList();

    private FilteredList<Fornecedor> filtrados;

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
        if (tabela != null) {
            recarregarTabela();
        }
    }

    @FXML
    public void initialize() {
        colunaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCNPJ.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        listaColuna.getItems().addAll("Todos", "ID", "Nome", "CNPJ", "Telefone");
        listaColuna.getSelectionModel().select("Todos");

        filtrados = new FilteredList<>(dados, f -> true);
        SortedList<Fornecedor> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(ordenados);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro());
        listaColuna.valueProperty().addListener((obs, antigo, novo) -> aplicarFiltro());

        criarColunaAcoes();
        recarregarTabela();
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
    void NovoFornecedor(ActionEvent event) {
        abrirFormulario(null);
    }

    private void criarColunaAcoes() {
        TableColumn<Fornecedor, Void> colunaAcoes = new TableColumn<>("Ações");
        colunaAcoes.setSortable(false);
        colunaAcoes.setPrefWidth(110);

        colunaAcoes.setCellFactory(coluna -> new TableCell<Fornecedor, Void>() {
            private final Button btnEditar = new Button("✎");
            private final Button btnExcluir = new Button("🗑");
            private final HBox caixa = new HBox(8, btnEditar, btnExcluir);

            {
                caixa.setAlignment(Pos.CENTER);
                String estilo = "-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14;";
                btnEditar.setStyle(estilo);
                btnExcluir.setStyle(estilo);
                btnEditar.setTooltip(new Tooltip("Editar"));
                btnExcluir.setTooltip(new Tooltip("Excluir"));

                btnEditar.setOnAction(e -> abrirFormulario(getTableRow().getItem()));
                btnExcluir.setOnAction(e -> excluir(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow().getItem() == null ? null : caixa);
            }
        });

        tabela.getColumns().add(colunaAcoes);
    }

    private void abrirFormulario(Fornecedor fornecedor) {
        try {
            java.net.URL url = getClass().getResource("/ui/admin/cadastroFornecedor-admin.fxml");
            if (url == null) {
                Alerta.exibirAlertaErro("Erro ao abrir formulário",
                        "Arquivo não encontrado: /ui/admin/cadastroFornecedor-admin.fxml");
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
            e.printStackTrace();
            Throwable causa = e;
            while (causa.getCause() != null) causa = causa.getCause();
            Alerta.exibirAlertaErro("Erro ao abrir formulário", causa.toString());
        }
    }

    private void excluir(Fornecedor fornecedor) {
        if (fornecedor == null) return;

        boolean confirmou = Alerta.confirmar("Excluir fornecedor",
                "Deseja excluir o fornecedor \"" + fornecedor.getNome() + "\" (ID " + fornecedor.getId() + ")?");
        if (!confirmou) return;

        try {
            facade.deletarFornecedor(fornecedor.getId());
            recarregarTabela();
            Alerta.exibirAlertaSucesso("Fornecedor removido.");
        } catch (RuntimeException e) {
            Alerta.exibirAlertaErro("Erro ao excluir fornecedor", e.getMessage());
        }
    }



    private void recarregarTabela() {
        if (facade == null) return;
        dados.setAll(facade.listarFornecedor().values());
        tabela.refresh();
    }

}
