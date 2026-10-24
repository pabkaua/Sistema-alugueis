package com.loja.controllers;

import com.loja.model.Item;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import java.util.Map;

public class DisponiveisClienteController {

    private FilteredList<Item> listaItensFiltrada;

    @FXML private Button botaoBuscar;
    @FXML private TextField campoBusca;
    @FXML private TableColumn<Item, String> colunaID;
    @FXML private TableColumn<Item, String> colunaItem;
    @FXML private TableColumn<Item, String> colunaCategoria;
    @FXML private TableColumn<Item, String> colunaValor;
    @FXML private ComboBox<String> listaColuna;
    @FXML private TableView<Item> tabela;

    public void initialize(){
        listaColuna.getItems().addAll("Todas", "ID", "Item", "Categoria", "Valor diário");
        listaColuna.setValue("Todas");

        colunaID.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getId()));
        colunaItem.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getNome()));
        colunaCategoria.setCellValueFactory(dado -> {
            var categoria = dado.getValue().getCategoria();
            String categoriaNome = (categoria != null && categoria.getNome() != null) ? categoria.getNome() : "Sem categoria";
            return new SimpleStringProperty(categoriaNome);
        });
        colunaValor.setCellValueFactory(dado -> {
            BigDecimal taxa = dado.getValue().getTaxaDiaria();
            String valorFormatado = taxa != null ? String.format("R$ %.2f", taxa).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });

        campoBusca.setOnAction(this::buscar);
    }

    public void setDados(ILojaFacade facade) {
        Map<String, Item> mapDisponiveisBruta = facade.listarItensDisponiveis();

        ObservableList<Item> listaItensBruta;
        if (mapDisponiveisBruta != null) {
            listaItensBruta = FXCollections.observableArrayList(mapDisponiveisBruta.values());
        } else {
            listaItensBruta = FXCollections.observableArrayList();
        }

        this.listaItensFiltrada = new FilteredList<>(listaItensBruta, item -> true);
        SortedList<Item> listaItensOrdenada = new SortedList<>(this.listaItensFiltrada);
        listaItensOrdenada.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(listaItensOrdenada);
    }

    @FXML
    void buscar(ActionEvent event) {
        if (this.listaItensFiltrada == null) {
            return;
        }

        String valorBuscado = campoBusca.getText();
        String colunaEscolhida = listaColuna.getValue();

        if(valorBuscado == null || valorBuscado.isBlank()){
            this.listaItensFiltrada.setPredicate(item -> true);
            return;
        }

        String formatado = valorBuscado.trim().toLowerCase();
        this.listaItensFiltrada.setPredicate(item ->{

            String id = item.getId() != null ? item.getId().toLowerCase() : "";
            String nomeItem = item.getNome() != null ? item.getNome().toLowerCase() : "";
            String categoria = (item.getCategoria() != null && item.getCategoria().getNome() != null) ? item.getCategoria().getNome().toLowerCase() : "";
            String valor = item.getTaxaDiaria() != null ? String.format("%.2f", item.getTaxaDiaria()).replace(".", ",") : "";

            if (colunaEscolhida == null || colunaEscolhida.equalsIgnoreCase("Todas")){
                return id.contains(formatado)
                        || nomeItem.contains(formatado)
                        || categoria.contains(formatado)
                        || valor.contains(formatado);
            }

            return switch (colunaEscolhida) {
                case "ID" -> id.contains(formatado);
                case "Item" -> nomeItem.contains(formatado);
                case "Categoria" -> categoria.contains(formatado);
                case "Valor diário" -> valor.contains(formatado);
                default -> false;
            };
        });
    }
}
