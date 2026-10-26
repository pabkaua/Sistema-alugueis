package com.loja.controllers.admin;

import com.loja.model.Categoria;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategAdminController {

    @FXML
    private TextField IdCategoria;

    @FXML
    private TextField NomeCategoria;

    @FXML
    private Button botaoBuscar;

    @FXML
    private Button botaoCateAdd;

    @FXML
    private Button botaoEditar;

    @FXML
    private Button botaoExcluir;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableColumn<Categoria, String> colunaID;

    @FXML
    private TableColumn<Categoria, String> colunaNome;

    @FXML
    private TableColumn<Categoria, String> colunaSelecionar;

    @FXML
    private ComboBox<String> listaColuna;

    @FXML
    private TableView<Categoria> tabela;

    private ILojaFacade facade;

    private final ObservableList<Categoria> dados = FXCollections.observableArrayList();
    private FilteredList<Categoria> filtrados;

    private final ObjectProperty<Categoria> selecionada = new SimpleObjectProperty<>();

    private final BooleanProperty modoEdicao = new SimpleBooleanProperty(false);
    private Categoria emEdicao;

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
        //recarregarTabela();
    }

    private void configurarColunas() {
        colunaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        filtrados = new FilteredList<>(dados, c -> true);
        tabela.setItems(filtrados);
        tabela.setPlaceholder(new javafx.scene.control.Label("Não há conteúdo na tabela"));
    }





    @FXML
    void buscar(ActionEvent event) {

    }

}