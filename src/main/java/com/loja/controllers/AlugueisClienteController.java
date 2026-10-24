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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AlugueisClienteController {

    Cliente usuario;
    ILojaFacade facade;
    Map<String, ContratoAluguel> mapContratosBruta;

    private ObservableList<ContratoAluguel> listaContratosBruta;
    private FilteredList<ContratoAluguel> listaContratosFiltrada;

    @FXML
    private Button botaoBuscar;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<ContratoAluguel, String> colunaDataDevolucao;

    @FXML
    private TableColumn<ContratoAluguel, String> colunaDataLocacao;

    @FXML
    private TableColumn<ContratoAluguel, String> colunaID;

    @FXML
    private TableColumn<ContratoAluguel, String> colunaItem;

    @FXML
    private ComboBox<String> listaColuna;

    @FXML
    private TableView<ContratoAluguel> tabelaAlugueis;

    public void initialize() {
        listaColuna.getItems().addAll("Todas", "ID", "Item", "Data Locação", "Data Devolução");
        listaColuna.setValue("Todas");

        colunaID.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getId()));

        colunaItem.setCellValueFactory(dado -> {
            Item item = dado.getValue().getItem();
            return new SimpleStringProperty(item != null ? item.getNome() : "");
        });

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colunaDataLocacao.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataRetirada();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });

        colunaDataDevolucao.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataPrevDevolucao();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });
    }

    public void setDados(Cliente usuario, ILojaFacade facade) {
        this.usuario = usuario;
        this.facade = facade;
        this.mapContratosBruta = facade.consultarHistoricoCliente(usuario.getId());

        if (this.mapContratosBruta != null) {
            this.listaContratosBruta = FXCollections.observableArrayList(this.mapContratosBruta.values());
        } else {
            this.listaContratosBruta = FXCollections.observableArrayList();
        }

        this.listaContratosFiltrada = new FilteredList<>(this.listaContratosBruta, aluguel -> true);
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

            if (colunaEscolhida == null || colunaEscolhida.equalsIgnoreCase("Todas")) {
                // algum desses vai retornar true caso encontre o valor
                return id.contains(formatado)
                        || nomeItem.contains(formatado)
                        || dataLocacao.contains(formatado)
                        || dataDevolucao.contains(formatado);
            }

            return switch (colunaEscolhida) {
                case "ID" -> id.contains(formatado);
                case "Item" -> nomeItem.contains(formatado);
                case "Data Locação" -> dataLocacao.contains(formatado);
                case "Data Devolução" -> dataDevolucao.contains(formatado);
                default -> false;
            };
        });
    }
}
