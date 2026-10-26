package com.loja.controllers.admin;

import com.loja.model.Item;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;

public class GerenciarItensController {

    ILojaFacade facade;

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

    public void setContexto(ILojaFacade facade){
        this.facade = facade;
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
        adminColunaCategoria.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getCategoria().getNome()));
        adminColunaFornecedor.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getFornecedor().getNome()));

        btnAdminEditar.disableProperty().bind(adminTabelaItens.getSelectionModel().selectedItemProperty().isNull());
        btnAdminDeletar.disableProperty().bind(adminTabelaItens.getSelectionModel().selectedItemProperty().isNull());

        adminTabelaItens.setPlaceholder(new Label("Nenhum item encontrado."));
        // filtroDeBusca();
    }

    @FXML
    void acaoAdicionar(ActionEvent event) {

    }

    @FXML
    void acaoDeletar(ActionEvent event) {

    }

    @FXML
    void acaoEditar(ActionEvent event) {

    }

}
