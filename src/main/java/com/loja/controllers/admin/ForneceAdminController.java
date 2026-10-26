package com.loja.controllers.admin;

import com.loja.model.Fornecedor;
import com.loja.padraofacade.interfaces.ILojaFacade;
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
import javafx.scene.control.cell.PropertyValueFactory;

public class ForneceAdminController {

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

    @FXML public void initialize(){
        colunaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCNPJ.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        listaColuna.getItems().addAll("Todos", "ID", "Nome","CNPJ", "Telefone");
        listaColuna.getSelectionModel().select("Todos");

        filtrados = new FilteredList<>(dados, f ->true);
        SortedList<Fornecedor> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(ordenados);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro());




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

    }

}
