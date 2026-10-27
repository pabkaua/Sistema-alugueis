package com.loja.controllers.funcionario;

import com.loja.controllers.Alerta;
import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.model.Usuario;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Map;

public class RegistrarAluguelFuncionarioController {

    private static final ZoneId ZONA_PADRAO = ZoneId.of("America/Sao_Paulo");

    private ILojaFacade facade;

    @FXML private Button botaoLimpar;
    @FXML private Button botaoRegistrar;
    @FXML private ComboBox<Cliente> listaClientes;
    @FXML private ComboBox<Item> listaItens;
    @FXML private DatePicker campoDataRetirada;
    @FXML private DatePicker campoDataDevolucao;
    @FXML private Label labelValorEstimado;

    public void initialize() {
        listaClientes.setConverter(new StringConverter<>() {
            @Override
            public String toString(Cliente cliente) {
                return cliente != null ? cliente.getNome() + " (" + cliente.getId() + ")" : "";
            }

            @Override
            public Cliente fromString(String texto) {
                return null;
            }
        });

        listaItens.setConverter(new StringConverter<>() {
            @Override
            public String toString(Item item) {
                if (item == null) {
                    return "";
                }
                String taxa = item.getTaxaDiaria() != null ? String.format("R$ %.2f", item.getTaxaDiaria()).replace(".", ",") : "R$ 0,00";
                return item.getNome() + " - " + taxa + "/dia";
            }

            @Override
            public Item fromString(String texto) {
                return null;
            }
        });

        campoDataRetirada.setValue(LocalDate.now(ZONA_PADRAO));

        listaItens.valueProperty().addListener((obs, antigo, novo) -> atualizarValorEstimado());
        campoDataRetirada.valueProperty().addListener((obs, antigo, novo) -> atualizarValorEstimado());
        campoDataDevolucao.valueProperty().addListener((obs, antigo, novo) -> atualizarValorEstimado());
        atualizarValorEstimado();
    }

    public void setDados(ILojaFacade facade) {
        this.facade = facade;
        carregarListas();
    }

    private void carregarListas() {
        Map<String, Usuario> mapClientesBruta = facade.listarUsuarioPorPerfil("CLIENTE");

        ObservableList<Cliente> listaClientesBruta;
        if (mapClientesBruta != null) {
            listaClientesBruta = FXCollections.observableArrayList(mapClientesBruta.values().stream()
                    .filter(Cliente.class::isInstance)
                    .map(Cliente.class::cast)
                    .sorted(Comparator.comparing(Cliente::getNome))
                    .toList());
        } else {
            listaClientesBruta = FXCollections.observableArrayList();
        }
        listaClientes.setItems(listaClientesBruta);

        Map<String, Item> mapItensBruta = facade.listarItensDisponiveis();

        ObservableList<Item> listaItensBruta;
        if (mapItensBruta != null) {
            listaItensBruta = FXCollections.observableArrayList(mapItensBruta.values().stream()
                    .sorted(Comparator.comparing(Item::getNome))
                    .toList());
        } else {
            listaItensBruta = FXCollections.observableArrayList();
        }
        listaItens.setItems(listaItensBruta);
    }

    private void atualizarValorEstimado() {
        Item item = listaItens.getValue();
        LocalDate retirada = campoDataRetirada.getValue();
        LocalDate devolucao = campoDataDevolucao.getValue();

        if (item == null || item.getTaxaDiaria() == null || retirada == null || devolucao == null || !devolucao.isAfter(retirada)) {
            labelValorEstimado.setText("R$ 0,00");
            return;
        }

        long dias = ChronoUnit.DAYS.between(retirada, devolucao);
        BigDecimal valorTotal = item.getTaxaDiaria().multiply(BigDecimal.valueOf(dias));
        labelValorEstimado.setText(String.format("R$ %.2f", valorTotal).replace(".", ","));
    }

    private void limparFormulario() {
        listaClientes.setValue(null);
        listaItens.setValue(null);
        campoDataRetirada.setValue(LocalDate.now(ZONA_PADRAO));
        campoDataDevolucao.setValue(null);
    }

    private void mostrarInfo(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    @FXML
    void registrarAluguel(ActionEvent event) {
        Cliente cliente = listaClientes.getValue();
        Item item = listaItens.getValue();
        LocalDate retirada = campoDataRetirada.getValue();
        LocalDate devolucao = campoDataDevolucao.getValue();

        if (cliente == null || item == null || retirada == null || devolucao == null) {
            Alerta.exibirAlertaAviso("Preencha todos os campos para registrar o aluguel.");
            return;
        }

        if (!devolucao.isAfter(retirada)) {
            Alerta.exibirAlertaAviso("A data de devolução deve ser posterior à data de retirada.");
            return;
        }

        try {
            ContratoAluguel contrato = facade.registrarAluguel(cliente.getId(), item.getId(), retirada, devolucao);
            mostrarInfo("Aluguel registrado! Contrato: " + contrato.getId());
            carregarListas();
            limparFormulario();
        } catch (RuntimeException e) {
            Alerta.exibirAlertaErro("Erro ao registrar aluguel", e.getMessage());
        }
    }

    @FXML
    void limpar(ActionEvent event) {
        limparFormulario();
    }
}