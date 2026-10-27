package com.loja.controllers.admin;

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

public class RelatorioAlugueisAtivosController {

    private FilteredList<ContratoAluguel> listaContratosFiltrada;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Button botaoBuscar;
    @FXML private TextField campoBusca;
    @FXML private TableColumn<ContratoAluguel, String> colunaID;
    @FXML private TableColumn<ContratoAluguel, String> colunaCliente;
    @FXML private TableColumn<ContratoAluguel, String> colunaItem;
    @FXML private TableColumn<ContratoAluguel, String> colunaRetirada;
    @FXML private TableColumn<ContratoAluguel, String> colunaDevolucao;
    @FXML private TableColumn<ContratoAluguel, String> colunaValor;
    @FXML private TableColumn<ContratoAluguel, String> colunaStatus;
    @FXML private ComboBox<String> listaColuna;
    @FXML private TableView<ContratoAluguel> tabela;

    public void initialize() {
        listaColuna.getItems().addAll("Todas", "ID", "Cliente", "Item", "Retirada", "Devolução prevista", "Valor total", "Status");
        listaColuna.setValue("Todas");

        colunaID.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getId()));
        colunaCliente.setCellValueFactory(dado -> {
            var cliente = dado.getValue().getCliente();
            String nomeCliente = (cliente != null && cliente.getNome() != null) ? cliente.getNome() : "";
            return new SimpleStringProperty(nomeCliente);
        });
        colunaItem.setCellValueFactory(dado -> {
            Item item = dado.getValue().getItem();
            return new SimpleStringProperty(item != null ? item.getNome() : "");
        });
        colunaRetirada.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataRetirada();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });
        colunaDevolucao.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataPrevDevolucao();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });
        colunaValor.setCellValueFactory(dado -> {
            BigDecimal total = dado.getValue().getValorTotal();
            String valorFormatado = total != null ? String.format("R$ %.2f", total).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });
        colunaStatus.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getStatus()));

        campoBusca.setOnAction(this::buscar);
    }

    public void setDados(ILojaFacade facade) {
        Map<String, ContratoAluguel> mapContratosBruta = facade.listarContratosAtivos();

        ObservableList<ContratoAluguel> listaContratosBruta;
        if (mapContratosBruta != null) {
            listaContratosBruta = FXCollections.observableArrayList(mapContratosBruta.values());
        } else {
            listaContratosBruta = FXCollections.observableArrayList();
        }

        this.listaContratosFiltrada = new FilteredList<>(listaContratosBruta, contrato -> true);
        SortedList<ContratoAluguel> listaContratosOrdenada = new SortedList<>(this.listaContratosFiltrada);
        listaContratosOrdenada.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(listaContratosOrdenada);
    }

    @FXML
    void buscar(ActionEvent event) {
        String valorBuscado = campoBusca.getText();
        String colunaEscolhida = listaColuna.getValue();

        if (valorBuscado == null || valorBuscado.isBlank()) {
            this.listaContratosFiltrada.setPredicate(contrato -> true);
            return;
        }

        String formatado = valorBuscado.trim().toLowerCase();
        this.listaContratosFiltrada.setPredicate(contrato -> {

            String id = contrato.getId() != null ? contrato.getId().toLowerCase() : "";
            String cliente = (contrato.getCliente() != null && contrato.getCliente().getNome() != null) ? contrato.getCliente().getNome().toLowerCase() : "";
            String item = (contrato.getItem() != null && contrato.getItem().getNome() != null) ? contrato.getItem().getNome().toLowerCase() : "";
            String retirada = contrato.getDataRetirada() != null ? contrato.getDataRetirada().format(formatador) : "";
            String devolucao = contrato.getDataPrevDevolucao() != null ? contrato.getDataPrevDevolucao().format(formatador) : "";
            String valor = contrato.getValorTotal() != null ? String.format("%.2f", contrato.getValorTotal()).replace(".", ",") : "";
            String status = contrato.getStatus() != null ? contrato.getStatus().toLowerCase() : "";

            if (colunaEscolhida == null || colunaEscolhida.equalsIgnoreCase("Todas")) {
                return id.contains(formatado)
                        || cliente.contains(formatado)
                        || item.contains(formatado)
                        || retirada.contains(formatado)
                        || devolucao.contains(formatado)
                        || valor.contains(formatado)
                        || status.contains(formatado);
            }

            return switch (colunaEscolhida) {
                case "ID" -> id.contains(formatado);
                case "Cliente" -> cliente.contains(formatado);
                case "Item" -> item.contains(formatado);
                case "Retirada" -> retirada.contains(formatado);
                case "Devolução prevista" -> devolucao.contains(formatado);
                case "Valor total" -> valor.contains(formatado);
                case "Status" -> status.contains(formatado);
                default -> false;
            };
        });
    }
}
