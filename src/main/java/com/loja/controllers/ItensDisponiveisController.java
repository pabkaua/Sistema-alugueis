package com.loja.controllers;

import java.util.List;
import java.util.Map;

import com.loja.model.Item;
import com.loja.padraofacade.interfaces.ILojaFacade;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ItensDisponiveisController {

    private static final List<String> COLUNAS = List.of("ID", "Item", "Categoria", "Valor diário");

    private ILojaFacade facade;
    private List<Item> todosItens = List.of();

    @FXML private ComboBox<String> listaColuna;
    @FXML private TextField campoBusca;
    @FXML private TableView<Item> tabela;
    @FXML private TableColumn<Item, String> colunaID;
    @FXML private TableColumn<Item, String> colunaItem;
    @FXML private TableColumn<Item, String> colunaCategoria;
    @FXML private TableColumn<Item, String> colunaValor;

    @FXML
    void initialize() {
        colunaID.setCellValueFactory(c -> new SimpleStringProperty(valorDaColuna(c.getValue(), "ID")));
        colunaItem.setCellValueFactory(c -> new SimpleStringProperty(valorDaColuna(c.getValue(), "Item")));
        colunaCategoria.setCellValueFactory(c -> new SimpleStringProperty(valorDaColuna(c.getValue(), "Categoria")));
        colunaValor.setCellValueFactory(c -> new SimpleStringProperty(valorDaColuna(c.getValue(), "Valor diário")));

        listaColuna.getItems().add("Todas");
        listaColuna.getItems().addAll(COLUNAS);
        listaColuna.setValue("Todas");

        Label mensagem = new Label("Não há itens disponíveis para aluguel no momento.");
        mensagem.setStyle("-fx-text-fill: white;");
        tabela.setPlaceholder(mensagem);
    }

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
    }

    public void carregarItens() {
        try {
            Map<String, Item> itens = facade.listarItensDisponiveis();
            todosItens = List.copyOf(itens.values());
            tabela.setItems(FXCollections.observableArrayList(todosItens));
        } catch (RuntimeException e) {
            Alerta.exibirAlertaAviso("Erro ao listar itens: " + e.getMessage());
        }
    }

    @FXML
    void buscar(ActionEvent event) {
        String coluna = listaColuna.getValue();
        String texto = campoBusca.getText().trim().toLowerCase();

        List<Item> filtrados = todosItens.stream()
                .filter(item -> combina(item, coluna, texto))
                .toList();

        tabela.setItems(FXCollections.observableArrayList(filtrados));
    }

    private boolean combina(Item item, String coluna, String texto) {
        if (coluna.equals("Todas")) {
            for (String nome : COLUNAS) {
                if (valorDaColuna(item, nome).toLowerCase().contains(texto)) {
                    return true;
                }
            }
            return false;
        }
        return valorDaColuna(item, coluna).toLowerCase().contains(texto);
    }

    private String valorDaColuna(Item item, String coluna) {
        return switch (coluna) {
            case "ID" -> String.valueOf(item.getId());
            case "Item" -> String.valueOf(item.getNome());
            case "Categoria" -> String.valueOf(item.getCategoria());
            default -> String.valueOf(item.getTaxaDiaria());
        };
    }
}
