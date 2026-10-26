package com.loja.controllers;

import java.util.Map;

import com.loja.model.ContratoAluguel;
import com.loja.padraofacade.interfaces.ILojaFacade;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ContratosPorClienteController {

    private ILojaFacade facade;

    @FXML private TextField campoBusca;
    @FXML private TableView<ContratoAluguel> tabela;
    @FXML private TableColumn<ContratoAluguel, String> colunaID;
    @FXML private TableColumn<ContratoAluguel, String> colunaItem;
    @FXML private TableColumn<ContratoAluguel, String> colunaValor;
    @FXML private TableColumn<ContratoAluguel, String> colunaStatus;
    @FXML private TableColumn<ContratoAluguel, String> colunaDevolucao;

    @FXML
    void initialize() {
        colunaID.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colunaItem.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getItem().getNome())));
        colunaValor.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getValorTotal())));
        colunaStatus.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStatus())));
        colunaDevolucao.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getDataPrevDevolucao())));

        definirMensagem("Digite o ID de um cliente para ver os contratos.");
    }

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
    }

    @FXML
    void buscarContratos(ActionEvent event) {
        String clienteId = campoBusca.getText().trim();

        if (clienteId.isEmpty()) {
            Alerta.exibirAlertaAviso("Digite o ID do cliente.");
            return;
        }

        try {
            Map<String, ContratoAluguel> contratos = facade.consultarHistoricoCliente(clienteId);
            tabela.setItems(FXCollections.observableArrayList(contratos.values()));
            definirMensagem("Não há histórico de contratos para esse cliente.");
        } catch (RuntimeException e) {
            Alerta.exibirAlertaAviso("Erro ao listar contratos: " + e.getMessage());
        }
    }

    private void definirMensagem(String texto) {
        Label mensagem = new Label(texto);
        mensagem.setStyle("-fx-text-fill: white;");
        tabela.setPlaceholder(mensagem);
    }
}
