package com.loja.controllers;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.model.Multa;
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
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MultasClienteController {

    private FilteredList<Multa> listaMultasFiltrada;

    @FXML private Button botaoBuscar;
    @FXML private TextField campoBusca;
    @FXML private TableColumn<Multa, String> colunaID;
    @FXML private TableColumn<Multa, String> colunaItem;
    @FXML private TableColumn<Multa, String> colunaValor;
    @FXML private TableColumn<Multa, String> colunaAtraso;
    @FXML private TableColumn<Multa, String> colunaIdContrato;
    @FXML private TableColumn<Multa, String> colunaStatus;
    @FXML private ComboBox<String> listaColuna;
    @FXML private TableView<Multa> tabelaMultas;

    public void initialize(){
        listaColuna.getItems().addAll("Todas", "ID", "Item", "Valor", "Dias em atraso", "Id do contrato", "Status");
        listaColuna.setValue("Todas");

        colunaID.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getId()));
        colunaItem.setCellValueFactory(dado -> {
            ContratoAluguel contrato = dado.getValue().getContrato();
            String nomeItem = (contrato != null && contrato.getItem() != null && contrato.getItem().getNome() != null) ? contrato.getItem().getNome()   : "-";
            return new SimpleStringProperty(nomeItem);
        });
        colunaValor.setCellValueFactory(dado -> {
            BigDecimal valorMulta = dado.getValue().getValorTotal();
            String valorFormatado = valorMulta != null ? String.format("R$ %.2f", valorMulta).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });
        colunaAtraso.setCellValueFactory(dado -> new SimpleStringProperty(String.valueOf(dado.getValue().getDiasAtraso())));
        colunaIdContrato.setCellValueFactory(dado -> {
            ContratoAluguel contrato = dado.getValue().getContrato();
            return new SimpleStringProperty(contrato != null && contrato.getId() != null ? contrato.getId() : "-");
        });
        colunaStatus.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getStatus()));

        campoBusca.setOnAction(this::buscarMulta);
    }

    public void setDados(Cliente usuario, ILojaFacade facade) {
        Map<String, Multa> mapMultasBruta = facade.listarMultaPorCliente(usuario.getId());

        ObservableList<Multa> listaMultasBruta;
        if (mapMultasBruta != null) {
            listaMultasBruta = FXCollections.observableArrayList(mapMultasBruta.values());
        } else {
            listaMultasBruta = FXCollections.observableArrayList();
        }

        this.listaMultasFiltrada = new FilteredList<>(listaMultasBruta, multa -> true);
        SortedList<Multa> listaMultasOrdenada = new SortedList<>(this.listaMultasFiltrada);
        listaMultasOrdenada.comparatorProperty().bind(tabelaMultas.comparatorProperty());
        tabelaMultas.setItems(listaMultasOrdenada);
    }

    @FXML
    void buscarMulta(ActionEvent event) {
        if (this.listaMultasFiltrada == null) {
            return;
        }

        String valorBuscado = campoBusca.getText();
        String colunaEscolhida = listaColuna.getValue();

        if (valorBuscado == null || valorBuscado.isBlank()) {
            this.listaMultasFiltrada.setPredicate(multa -> true);
            return;
        }

        String formatado = valorBuscado.trim().toLowerCase();

        this.listaMultasFiltrada.setPredicate(multa -> {

            String id = multa.getId() != null ? multa.getId().toLowerCase() : "";
            String nomeItem = (multa.getContrato().getItem() != null && multa.getContrato().getItem().getNome() != null) ? multa.getContrato().getItem().getNome().toLowerCase() : "";
            String valor = multa.getValorTotal() != null ? String.format("%.2f", multa.getValorTotal()).replace(".", ",") : "";
            String diasAtraso = String.valueOf(multa.getDiasAtraso());
            String idContrato = (multa.getContrato() != null && multa.getContrato().getId() != null) ? multa.getContrato().getId().toLowerCase() : "";
            String status = multa.getStatus() != null ? multa.getStatus().toLowerCase() : "";

            if (colunaEscolhida == null || colunaEscolhida.equalsIgnoreCase("Todas")) {
                // algum desses vai retornar true caso encontre o valor
                return id.contains(formatado)
                        || nomeItem.contains(formatado)
                        || valor.contains(formatado)
                        || diasAtraso.contains(formatado)
                        || idContrato.contains(formatado)
                        || status.contains(formatado);
            }

            return switch (colunaEscolhida) {
                case "ID" -> id.contains(formatado);
                case "Item" -> nomeItem.contains(formatado);
                case "Valor" -> valor.contains(formatado);
                case "Dias em atraso" -> diasAtraso.contains(formatado);
                case "Id do contrato" -> idContrato.contains(formatado);
                case "Status" -> status.contains(formatado);
                default -> false;
            };
        });
    }
}