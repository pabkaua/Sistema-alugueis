package com.loja.controllers.funcionario;

import com.loja.controllers.Alerta;
import com.loja.model.ContratoAluguel;
import com.loja.model.Multa;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.Map;

public class QuitarMultaFuncionarioController {

    private ILojaFacade facade;
    private FilteredList<Multa> listaMultasFiltrada;
    private final ObservableList<Multa> listaMultasBruta = FXCollections.observableArrayList();

    @FXML private Button botaoBuscar;
    @FXML private Button botaoQuitarMulta;
    @FXML private TextField campoBusca;
    @FXML private TableColumn<Multa, String> colunaID;
    @FXML private TableColumn<Multa, String> colunaCliente;
    @FXML private TableColumn<Multa, String> colunaItem;
    @FXML private TableColumn<Multa, String> colunaIdContrato;
    @FXML private TableColumn<Multa, String> colunaAtraso;
    @FXML private TableColumn<Multa, String> colunaValor;
    @FXML private TableColumn<Multa, String> colunaStatus;
    @FXML private ComboBox<String> listaColuna;
    @FXML private TableView<Multa> tabelaMultas;

    public void initialize() {
        listaColuna.getItems().addAll("Todas", "ID", "Cliente", "Item", "Id do contrato", "Dias em atraso", "Valor", "Status");
        listaColuna.setValue("Todas");

        colunaID.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getId()));
        colunaCliente.setCellValueFactory(dado -> {
            ContratoAluguel contrato = dado.getValue().getContrato();
            String nomeCliente = (contrato != null && contrato.getCliente() != null && contrato.getCliente().getNome() != null) ? contrato.getCliente().getNome() : "-";
            return new SimpleStringProperty(nomeCliente);
        });
        colunaItem.setCellValueFactory(dado -> {
            ContratoAluguel contrato = dado.getValue().getContrato();
            String nomeItem = (contrato != null && contrato.getItem() != null && contrato.getItem().getNome() != null) ? contrato.getItem().getNome() : "-";
            return new SimpleStringProperty(nomeItem);
        });
        colunaIdContrato.setCellValueFactory(dado -> {
            ContratoAluguel contrato = dado.getValue().getContrato();
            return new SimpleStringProperty(contrato != null && contrato.getId() != null ? contrato.getId() : "-");
        });
        colunaAtraso.setCellValueFactory(dado -> new SimpleStringProperty(String.valueOf(dado.getValue().getDiasAtraso())));
        colunaValor.setCellValueFactory(dado -> {
            BigDecimal valorMulta = dado.getValue().getValorTotal();
            String valorFormatado = valorMulta != null ? String.format("R$ %.2f", valorMulta).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });
        colunaStatus.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getStatus()));

        // só habilita o botão quando tem uma multa pendente selecionada
        botaoQuitarMulta.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            Multa selecionada = tabelaMultas.getSelectionModel().getSelectedItem();
            return selecionada == null || !"PENDENTE".equalsIgnoreCase(selecionada.getStatus());
        }, tabelaMultas.getSelectionModel().selectedItemProperty()));
        tabelaMultas.setPlaceholder(new Label("Nenhuma multa encontrada."));

        this.listaMultasFiltrada = new FilteredList<>(listaMultasBruta, multa -> true);
        SortedList<Multa> listaMultasOrdenada = new SortedList<>(this.listaMultasFiltrada);
        listaMultasOrdenada.comparatorProperty().bind(tabelaMultas.comparatorProperty());
        tabelaMultas.setItems(listaMultasOrdenada);

        campoBusca.setOnAction(this::buscarMulta);
    }

    public void setDados(ILojaFacade facade) {
        this.facade = facade;
        carregarTabela();
    }

    private void carregarTabela() {
        Map<String, Multa> mapMultasBruta = facade.listarMulta();

        if (mapMultasBruta != null) {
            listaMultasBruta.setAll(mapMultasBruta.values());
        } else {
            listaMultasBruta.clear();
        }
        tabelaMultas.refresh();
    }

    private void mostrarInfo(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    @FXML
    void quitarMulta(ActionEvent event) {
        Multa selecionada = tabelaMultas.getSelectionModel().getSelectedItem();
        if (selecionada == null) return;

        String nomeCliente = (selecionada.getContrato() != null && selecionada.getContrato().getCliente() != null && selecionada.getContrato().getCliente().getNome() != null) ? selecionada.getContrato().getCliente().getNome() : "-";
        String valorFormatado = selecionada.getValorTotal() != null ? String.format("R$ %.2f", selecionada.getValorTotal()).replace(".", ",") : "R$ 0,00";

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar quitação");
        confirmacao.setHeaderText("Quitar a multa do cliente: " + nomeCliente);
        confirmacao.setContentText("Valor a ser quitado: " + valorFormatado);

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                try {
                    facade.quitarMulta(selecionada.getId());
                    mostrarInfo("Multa quitada com sucesso!");
                    carregarTabela();
                } catch (RuntimeException e) {
                    Alerta.exibirAlertaErro("Erro ao quitar multa", e.getMessage());
                }
            }
        });
    }

    @FXML
    void buscarMulta(ActionEvent event) {
        String valorBuscado = campoBusca.getText();
        String colunaEscolhida = listaColuna.getValue();

        if (valorBuscado == null || valorBuscado.isBlank()) {
            this.listaMultasFiltrada.setPredicate(multa -> true);
            return;
        }

        String formatado = valorBuscado.trim().toLowerCase();

        this.listaMultasFiltrada.setPredicate(multa -> {

            ContratoAluguel contrato = multa.getContrato();

            String id = multa.getId() != null ? multa.getId().toLowerCase() : "";
            String nomeCliente = (contrato != null && contrato.getCliente() != null && contrato.getCliente().getNome() != null) ? contrato.getCliente().getNome().toLowerCase() : "";
            String nomeItem = (contrato != null && contrato.getItem() != null && contrato.getItem().getNome() != null) ? contrato.getItem().getNome().toLowerCase() : "";
            String idContrato = (contrato != null && contrato.getId() != null) ? contrato.getId().toLowerCase() : "";
            String diasAtraso = String.valueOf(multa.getDiasAtraso());
            String valor = multa.getValorTotal() != null ? String.format("%.2f", multa.getValorTotal()).replace(".", ",") : "";
            String status = multa.getStatus() != null ? multa.getStatus().toLowerCase() : "";

            if (colunaEscolhida == null || colunaEscolhida.equalsIgnoreCase("Todas")) {
                // algum desses vai retornar true caso encontre o valor
                return id.contains(formatado)
                        || nomeCliente.contains(formatado)
                        || nomeItem.contains(formatado)
                        || idContrato.contains(formatado)
                        || diasAtraso.contains(formatado)
                        || valor.contains(formatado)
                        || status.contains(formatado);
            }

            return switch (colunaEscolhida) {
                case "ID" -> id.contains(formatado);
                case "Cliente" -> nomeCliente.contains(formatado);
                case "Item" -> nomeItem.contains(formatado);
                case "Id do contrato" -> idContrato.contains(formatado);
                case "Dias em atraso" -> diasAtraso.contains(formatado);
                case "Valor" -> valor.contains(formatado);
                case "Status" -> status.contains(formatado);
                default -> false;
            };
        });
    }
}