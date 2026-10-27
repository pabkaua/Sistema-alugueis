package com.loja.controllers.admin;

import com.loja.controllers.Alerta;
import com.loja.exceptions.BusinessException;
import com.loja.model.Categoria;
import com.loja.model.Fornecedor;
import com.loja.model.Item;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class GerenciarItensController {

    private ILojaFacade facade;
    private StackPane conteudoPai;
    private FilteredList<Item> listaFiltrada;

    @FXML private TableColumn<Item, String> adminColunaId;
    @FXML private TableColumn<Item, String> adminColunaNome;
    @FXML private TableColumn<Item, String> adminColunaValorDiario;
    @FXML private TableColumn<Item, String> adminColunaValorRepo;
    @FXML private TableColumn<Item, String> adminColunaStatus;
    @FXML private TableColumn<Item, String> adminColunaCategoria;
    @FXML private TableColumn<Item, String> adminColunaFornecedor;
    @FXML private TableView<Item> adminTabelaItens;
    @FXML private Button btnAdminDeletar;
    @FXML private Button btnAdminEditar;
    @FXML private Button btnAdminNovo;
    @FXML private TextField campoBuscaItem;

    public void setContexto(ILojaFacade facade, StackPane conteudo){
        this.facade = facade;
        this.conteudoPai = conteudo;
        carregarTabela();
    }

    public void initialize(){
        adminColunaId.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getId()));
        adminColunaNome.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getNome()));
        adminColunaValorDiario.setCellValueFactory(item -> {
            BigDecimal taxa = item.getValue().getTaxaDiaria();
            String valorFormatado = taxa != null ? String.format("R$ %.2f", taxa).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });
        adminColunaValorRepo.setCellValueFactory(item -> {
            BigDecimal valorRepo = item.getValue().getValorReposicao();
            String valorFormatado = valorRepo != null ? String.format("R$ %.2f", valorRepo).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });
        adminColunaStatus.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getStatus()));
        adminColunaCategoria.setCellValueFactory(item -> {
            Categoria categoria = item.getValue().getCategoria();
            return new SimpleStringProperty(categoria != null && categoria.getNome() != null ? categoria.getNome() : "-");
        });

        adminColunaFornecedor.setCellValueFactory(item -> {
            Fornecedor fornecedor = item.getValue().getFornecedor();
            return new SimpleStringProperty(fornecedor != null && fornecedor.getNome() != null ? fornecedor.getNome() : "-");
        });
        btnAdminEditar.disableProperty().bind(adminTabelaItens.getSelectionModel().selectedItemProperty().isNull());
        btnAdminDeletar.disableProperty().bind(adminTabelaItens.getSelectionModel().selectedItemProperty().isNull());

        adminTabelaItens.setPlaceholder(new Label("Nenhum item encontrado."));
        filtroDeBusca();
    }

    @FXML
    void acaoAdicionar(ActionEvent event) {
        try {
            java.net.URL url = getClass().getResource("/ui/admin/criar-atualizar-itens.fxml");
            if (url == null){
                Alerta.exibirAlertaErro("Erro: 404 Not Found", " Arquivo criar-atualizar-itens.fxml não encontrado");
                return;
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(url);
            javafx.scene.Node tela = loader.load();

            CriarItemController controller = loader.getController();
            controller.setContexto(this.facade, this.conteudoPai);
            this.conteudoPai.getChildren().setAll(tela);
        } catch (IOException e){
            Alerta.exibirAlertaErro("Erro de carregamento", "Erro ao carregar a tela de usuarios");
        }
    }

    @FXML
    void acaoDeletar(ActionEvent event) {
        Item itemSelecionado = adminTabelaItens.getSelectionModel().getSelectedItem();
        try {
            facade.deletarItem(itemSelecionado.getId());
            carregarTabela();
        } catch (BusinessException e){
            Alerta.exibirAlertaErro("Erro ao deletar!", e.getMessage());
        }
    }

    @FXML
    void acaoEditar(ActionEvent event) {
        // fazer
    }

    private void filtroDeBusca() {
        campoBuscaItem.textProperty().addListener((observable, valorAntigo, novoValor) -> {

            if (listaFiltrada == null) return;
            if (novoValor == null || novoValor.isBlank()) {
                listaFiltrada.setPredicate(item -> true);
                return;
            }

            String termo = novoValor.trim().toLowerCase();

            listaFiltrada.setPredicate(item -> {
                if (item == null) {
                    return false;
                }

                String id = item.getId() != null ? item.getId().toLowerCase() : "";
                String nome = item.getNome() != null ? item.getNome().toLowerCase() : "";
                String status = item.getStatus() != null ? item.getStatus().toLowerCase() : "";
                String taxaDiaria = item.getTaxaDiaria() != null ? String.format("%.2f", item.getTaxaDiaria()).replace(".", ",") : "";
                String valorRepo = item.getValorReposicao() != null ? String.format("%.2f", item.getValorReposicao()).replace(".", ",") : "";
                String categoria = (item.getCategoria() != null && item.getCategoria().getNome() != null) ? item.getCategoria().getNome().toLowerCase() : "";
                String fornecedor = (item.getFornecedor() != null && item.getFornecedor().getNome() != null) ? item.getFornecedor().getNome().toLowerCase() : "";

                // algum desses vai retornar true caso encontre o valor
                return id.contains(termo)
                        || nome.contains(termo)
                        || status.contains(termo)
                        || taxaDiaria.contains(termo)
                        || valorRepo.contains(termo)
                        || categoria.contains(termo)
                        || fornecedor.contains(termo);
            });
        });
    }

    public void carregarTabela() {
        Map<String, Item> itensMap = facade.listarItem();

        ObservableList<Item> listaBruta;
        if (itensMap != null) {
            listaBruta = FXCollections.observableArrayList(itensMap.values());
        } else {
            listaBruta = FXCollections.observableArrayList();
        }

        this.listaFiltrada = new FilteredList<>(listaBruta, item -> true);

        SortedList<Item> listaOrdenada = new SortedList<>(this.listaFiltrada);
        listaOrdenada.comparatorProperty().bind(adminTabelaItens.comparatorProperty());
        adminTabelaItens.setItems(listaOrdenada);
    }
}