package com.loja.controllers.funcionario;

import com.loja.controllers.Alerta;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ContratosPorClienteController {

    private ILojaFacade facade;

    @FXML private Button botaoBuscar;
    @FXML private TextField campoBusca;
    @FXML private TableColumn<ContratoAluguel, String> colunaID;
    @FXML private TableColumn<ContratoAluguel, String> colunaItem;
    @FXML private TableColumn<ContratoAluguel, String> colunaValor;
    @FXML private TableColumn<ContratoAluguel, String> colunaStatus;
    @FXML private TableColumn<ContratoAluguel, String> colunaDevolucao;
    @FXML private TableView<ContratoAluguel> tabela;

    public void initialize() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colunaID.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getId()));
        colunaItem.setCellValueFactory(dado -> {
            Item item = dado.getValue().getItem();
            return new SimpleStringProperty(item != null ? item.getNome() : "");
        });
        colunaValor.setCellValueFactory(dado -> {
            BigDecimal total = dado.getValue().getValorTotal();
            String valorFormatado = total != null ? String.format("R$ %.2f", total).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });
        colunaStatus.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getStatus()));
        colunaDevolucao.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataPrevDevolucao();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });

        campoBusca.setOnAction(this::buscarContratos);
    }

    public void setDados(ILojaFacade facade) {
        this.facade = facade;
    }

    @FXML
    void buscarContratos(ActionEvent event) {
        String clienteId = campoBusca.getText();

        if (clienteId == null || clienteId.isBlank()) {
            Alerta.exibirAlertaAviso("Digite o ID do cliente.");
            return;
        }

        try {
            Map<String, ContratoAluguel> mapContratosBruta = facade.consultarHistoricoCliente(clienteId.trim());

            ObservableList<ContratoAluguel> listaContratosBruta;
            if (mapContratosBruta != null) {
                listaContratosBruta = FXCollections.observableArrayList(mapContratosBruta.values());
            } else {
                listaContratosBruta = FXCollections.observableArrayList();
            }

            SortedList<ContratoAluguel> listaContratosOrdenada = new SortedList<>(listaContratosBruta);
            listaContratosOrdenada.comparatorProperty().bind(tabela.comparatorProperty());
            tabela.setItems(listaContratosOrdenada);

        } catch (RuntimeException e) {
            Alerta.exibirAlertaErro("Erro ao listar contratos", e.getMessage());
        }
    }
}
