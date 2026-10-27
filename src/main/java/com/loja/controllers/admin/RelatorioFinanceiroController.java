package com.loja.controllers.admin;

import com.loja.controllers.Alerta;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RelatorioFinanceiroController {

    private ILojaFacade facade;
    private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private DatePicker dataInicial;
    @FXML private DatePicker dataFinal;
    @FXML private Button botaoGerar;
    @FXML private TextArea areaRelatorio;

    public void initialize() {
        configurarCampoData(dataInicial);
        configurarCampoData(dataFinal);
    }

    public void setDados(ILojaFacade facade) {
        this.facade = facade;
    }

    private void configurarCampoData(DatePicker campo) {
        campo.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate data) {
                return data != null ? data.format(formatador) : "";
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(texto, formatador);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });

        campo.getEditor().setTextFormatter(new TextFormatter<String>(mudanca -> {
            String numeros = mudanca.getControlNewText().replaceAll("\\D", "");
            if (numeros.length() > 8) {
                return null;
            }

            String texto = numeros;
            if (numeros.length() > 4) {
                texto = numeros.substring(0, 2) + "/" + numeros.substring(2, 4) + "/" + numeros.substring(4);
            } else if (numeros.length() > 2) {
                texto = numeros.substring(0, 2) + "/" + numeros.substring(2);
            }

            mudanca.setRange(0, mudanca.getControlText().length());
            mudanca.setText(texto);
            mudanca.selectRange(texto.length(), texto.length());
            return mudanca;
        }));
    }

    @FXML
    void gerar(ActionEvent event) {
        LocalDate inicio = dataInicial.getValue();
        LocalDate fim = dataFinal.getValue();

        if (inicio == null || fim == null) {
            Alerta.exibirAlertaAviso("Selecione a data inicial e a data final.");
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
