package com.loja.controllers.funcionario;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ContratosAtivosFuncionarioController {

    private static final ZoneId ZONA_PADRAO = ZoneId.of("America/Sao_Paulo");

    private FilteredList<ContratoAluguel> listaContratosFiltrada;

    @FXML private Button botaoBuscar;
    @FXML private TextField campoBusca;
    @FXML private Label labelResumo;
    @FXML private TableColumn<ContratoAluguel, String> colunaID;
    @FXML private TableColumn<ContratoAluguel, String> colunaCliente;
    @FXML private TableColumn<ContratoAluguel, String> colunaItem;
    @FXML private TableColumn<ContratoAluguel, String> colunaDataLocacao;
    @FXML private TableColumn<ContratoAluguel, String> colunaDataDevolucao;
    @FXML private TableColumn<ContratoAluguel, String> colunaSituacao;
    @FXML private TableColumn<ContratoAluguel, String> colunaValor;
    @FXML private ComboBox<String> listaColuna;
    @FXML private TableView<ContratoAluguel> tabelaContratos;

    public void initialize() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        listaColuna.getItems().addAll("Todas", "ID", "Cliente", "Item", "Data Locação", "Data Devolução", "Situação", "Valor Total");
        listaColuna.setValue("Todas");

        colunaID.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getId()));
        colunaCliente.setCellValueFactory(dado -> {
            Cliente cliente = dado.getValue().getCliente();
            return new SimpleStringProperty(cliente != null && cliente.getNome() != null ? cliente.getNome() : "-");
        });
        colunaItem.setCellValueFactory(dado -> {
            Item item = dado.getValue().getItem();
            return new SimpleStringProperty(item != null && item.getNome() != null ? item.getNome() : "-");
        });
        colunaDataLocacao.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataRetirada();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });
        colunaDataDevolucao.setCellValueFactory(dado -> {
            LocalDate data = dado.getValue().getDataPrevDevolucao();
            return new SimpleStringProperty(data != null ? data.format(formatador) : "");
        });
        colunaSituacao.setCellValueFactory(dado -> new SimpleStringProperty(descreverSituacao(dado.getValue())));
        colunaValor.setCellValueFactory(dado -> {
            BigDecimal taxa = dado.getValue().getValorTotal();
            String valorFormatado = taxa != null ? String.format("R$ %.2f", taxa).replace(".", ",") : "R$ 0,00";
            return new SimpleStringProperty(valorFormatado);
        });

        tabelaContratos.setPlaceholder(new Label("Nenhum contrato ativo encontrado."));

        campoBusca.setOnAction(this::buscarContrato);
    }

    public void setDados(ILojaFacade facade) {
        Map<String, ContratoAluguel> mapContratosBruta = facade.listarContratosAtivos();

        ObservableList<ContratoAluguel> listaContratosBruta;
        if (mapContratosBruta != null) {
            listaContratosBruta = FXCollections.observableArrayList(mapContratosBruta.values());
        } else {
            listaContratosBruta = FXCollections.observableArrayList();
        }

        long atrasados = listaContratosBruta.stream()
                .filter(contrato -> descreverSituacao(contrato).equals("Em atraso"))
                .count();
        labelResumo.setText("Total de contratos ativos: " + listaContratosBruta.size() + "  |  Em atraso: " + atrasados);

        this.listaContratosFiltrada = new FilteredList<>(listaContratosBruta, contrato -> true);
        SortedList<ContratoAluguel> listaContratosOrdenada = new SortedList<>(this.listaContratosFiltrada);
        listaContratosOrdenada.comparatorProperty().bind(tabelaContratos.comparatorProperty());
        tabelaContratos.setItems(listaContratosOrdenada);
    }

    private String descreverSituacao(ContratoAluguel contrato) {
        LocalDate hoje = LocalDate.now(ZONA_PADRAO);
        boolean emAtraso = contrato.getDataPrevDevolucao() != null && hoje.isAfter(contrato.getDataPrevDevolucao());
        return emAtraso ? "Em atraso" : "No prazo";
    }

    @FXML
    void buscarContrato(ActionEvent event) {
        if (this.listaContratosFiltrada == null) {
            return;
        }

        String valorBuscado = campoBusca.getText();
        String colunaEscolhida = listaColuna.getValue();

        if (valorBuscado == null || valorBuscado.isBlank()) {
            this.listaContratosFiltrada.setPredicate(contrato -> true);
            return;
        }

        String formatado = valorBuscado.trim().toLowerCase();
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        this.listaContratosFiltrada.setPredicate(contrato -> {

            String id = contrato.getId() != null ? contrato.getId().toLowerCase() : "";
            String nomeCliente = (contrato.getCliente() != null && contrato.getCliente().getNome() != null) ? contrato.getCliente().getNome().toLowerCase() : "";
            String nomeItem = (contrato.getItem() != null && contrato.getItem().getNome() != null) ? contrato.getItem().getNome().toLowerCase() : "";
            String dataLocacao = contrato.getDataRetirada() != null ? contrato.getDataRetirada().format(formatadorData) : "";
            String dataDevolucao = contrato.getDataPrevDevolucao() != null ? contrato.getDataPrevDevolucao().format(formatadorData) : "";
            String situacao = descreverSituacao(contrato).toLowerCase();
            String valor = contrato.getValorTotal() != null ? String.format("%.2f", contrato.getValorTotal()).replace(".", ",") : "";

            if (colunaEscolhida == null || colunaEscolhida.equalsIgnoreCase("Todas")) {
                // algum desses vai retornar true caso encontre o valor
                return id.contains(formatado)
                        || nomeCliente.contains(formatado)
                        || nomeItem.contains(formatado)
                        || dataLocacao.contains(formatado)
                        || dataDevolucao.contains(formatado)
                        || situacao.contains(formatado)
                        || valor.contains(formatado);
            }

            return switch (colunaEscolhida) {
                case "ID" -> id.contains(formatado);
                case "Cliente" -> nomeCliente.contains(formatado);
                case "Item" -> nomeItem.contains(formatado);
                case "Data Locação" -> dataLocacao.contains(formatado);
                case "Data Devolução" -> dataDevolucao.contains(formatado);
                case "Situação" -> situacao.contains(formatado);
                case "Valor Total" -> valor.contains(formatado);
                default -> false;
            };
        });
    }
}