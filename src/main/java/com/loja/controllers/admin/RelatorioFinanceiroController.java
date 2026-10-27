package com.loja.controllers.admin;

import com.loja.controllers.Alerta;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RelatorioFinanceiroController {

    private ILojaFacade facade;
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TextField campoDataInicial;
    @FXML private TextField campoDataFinal;
    @FXML private Button botaoGerar;
    @FXML private TextArea areaRelatorio;

    public void initialize() {
        campoDataFinal.setOnAction(this::gerar);
    }

    public void setDados(ILojaFacade facade) {
        this.facade = facade;
    }

    @FXML
    void gerar(ActionEvent event) {
        String textoInicial = campoDataInicial.getText();
        String textoFinal = campoDataFinal.getText();

        if (textoInicial == null || textoInicial.isBlank() || textoFinal == null || textoFinal.isBlank()) {
            Alerta.exibirAlertaAviso("Digite a data inicial e a data final.");
            return;
        }

        LocalDate inicio;
        LocalDate fim;
        try {
            inicio = LocalDate.parse(textoInicial.trim(), formatoData);
            fim = LocalDate.parse(textoFinal.trim(), formatoData);
        } catch (DateTimeParseException e) {
            Alerta.exibirAlertaAviso("Data inválida, tente novamente.");
            return;
        }

        if (fim.isBefore(inicio)) {
            Alerta.exibirAlertaAviso("Data final não pode ser anterior à data inicial.");
            return;
        }

        try {
            areaRelatorio.setText(facade.gerarRelatorioFaturamento(inicio, fim));
        } catch (RuntimeException e) {
            Alerta.exibirAlertaErro("Erro ao gerar relatório", e.getMessage());
        }
    }
}
