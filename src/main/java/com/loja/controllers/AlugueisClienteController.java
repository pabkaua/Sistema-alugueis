package com.loja.controllers;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AlugueisClienteController {

    private FilteredList<ContratoAluguel> listaContratosFiltrada;

    @FXML private Button botaoBuscar;
    @FXML private TextField campoBusca;
    @FXML private TableColumn<ContratoAluguel, String> colunaDataDevolucao;
    @FXML private TableColumn<ContratoAluguel, String> colunaDataLocacao;
    @FXML private TableColumn<ContratoAluguel, String> colunaID;
    @FXML private TableColumn<ContratoAluguel, String> colunaItem;
    @FXML private TableColumn<ContratoAluguel, String> colunaStatus;
    @FXML private TableColumn<ContratoAluguel, String> colunaValor;
    @FXML private ComboBox<String> listaColuna;
    @FXML private TableView<ContratoAluguel> tabelaAlugueis;

    public void initialize() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        listaColuna.getItems().addAll("Todas", "ID", "Item", "Data Locação", "Data Devolução", "Status", "Valor Total");
        listaColuna.setValue("Todas");
        colunaID.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getId()));
        colunaStatus.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getStatus()));
        colunaItem.setCellValueFactory(dado -> {
            Item item = dado.getValue().getItem();
            return new SimpleStringProperty(item != null ? item.getNome() : "");
        });
        colunaValor.setCellValueFactory(dado -> {
            BigDecimal taxa = dado.getValue().getValorTotal();
            String valorFormatado = taxa != null ? String.format("R$ %.2f", taxa).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });
        colunaDataLocacao.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataRetirada();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });
        colunaDataDevolucao.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataPrevDevolucao();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });

        campoBusca.setOnAction(this::buscarAluguel);
    }

    public void setDados(Cliente usuario, ILojaFacade facade) {

        Map<String, ContratoAluguel> mapContratosBruta = facade.consultarHistoricoCliente(usuario.getId());

        ObservableList<ContratoAluguel> listaContratosBruta;
        if (mapContratosBruta != null) {
            listaContratosBruta = FXCollections.observableArrayList(mapContratosBruta.values());
        } else {
            listaContratosBruta = FXCollections.observableArrayList();
        }

        this.listaContratosFiltrada = new FilteredList<>(listaContratosBruta, aluguel -> true);
        SortedList<ContratoAluguel> listaContratosOrdenada = new SortedList<>(this.listaContratosFiltrada);
        listaContratosOrdenada.comparatorProperty().bind(tabelaAlugueis.comparatorProperty());
        tabelaAlugueis.setItems(listaContratosOrdenada);
    }


    @FXML
    void buscarAluguel(ActionEvent event) {
        String valorBuscado = campoBusca.getText();
        String colunaEscolhida = listaColuna.getValue();

        if (valorBuscado == null || valorBuscado.isBlank()) {
            this.listaContratosFiltrada.setPredicate(aluguel -> true);
            return;
        }

        String formatado = valorBuscado.trim().toLowerCase();
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        this.listaContratosFiltrada.setPredicate(contrato -> {

            String id = contrato.getId() != null ? contrato.getId().toLowerCase() : "";
            String nomeItem = (contrato.getItem() != null && contrato.getItem().getNome() != null) ? contrato.getItem().getNome().toLowerCase() : "";
            String dataLocacao = contrato.getDataRetirada() != null ? contrato.getDataRetirada().format(formatadorData) : "";
            String dataDevolucao = contrato.getDataPrevDevolucao() != null ? contrato.getDataPrevDevolucao().format(formatadorData) : "";
            String status = contrato.getStatus() != null ? contrato.getStatus().toLowerCase() : "";
            String valor = contrato.getValorTotal() != null ? String.format("%.2f", contrato.getValorTotal()).replace(".", ",") : "";


            if (colunaEscolhida == null || colunaEscolhida.equalsIgnoreCase("Todas")) {
                // algum desses vai retornar true caso encontre o valor
                return id.contains(formatado)
                        || nomeItem.contains(formatado)
                        || dataLocacao.contains(formatado)
                        || dataDevolucao.contains(formatado)
                        || status.contains(formatado)
                        || valor.contains(formatado);
            }

            return switch (colunaEscolhida) {
                case "ID" -> id.contains(formatado);
                case "Item" -> nomeItem.contains(formatado);
                case "Data Locação" -> dataLocacao.contains(formatado);
                case "Data Devolução" -> dataDevolucao.contains(formatado);
                case "Status" -> status.contains(formatado);
                case "Valor Total" -> valor.contains(formatado);
                default -> false;
            };
        });
    }
}
