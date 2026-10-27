package com.loja.controllers.admin;

import com.loja.controllers.Alerta;
import com.loja.exceptions.BusinessException;
import com.loja.model.Categoria;
import com.loja.model.Fornecedor;
import com.loja.model.Item;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CriarItemController {

    private ILojaFacade facade;
    private StackPane conteudoPai;

    @FXML private Label tituloPagina;
    @FXML private TextField campoID;
    @FXML private TextField campoNome;
    @FXML private TextField campoTaxaDiaria;
    @FXML private TextField campoValorReposicao;
    @FXML private ComboBox<Categoria> listaCategoria;
    @FXML private ComboBox<Fornecedor> listaFornecedor;
    @FXML private Button botaoCancelar;
    @FXML private Button botaoCriar;

    public void initialize() {
        listaCategoria.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return (categoria != null && categoria.getNome() != null) ? categoria.getNome() : "";
            }

            @Override public Categoria fromString(String string) {return null;}
        });

        listaFornecedor.setConverter(new StringConverter<Fornecedor>() {
            @Override
            public String toString(Fornecedor fornecedor) {
                return (fornecedor != null && fornecedor.getNome() != null) ? fornecedor.getNome() : "";
            }

            @Override public Fornecedor fromString(String string) {return null;}
        });
    }

    public void setContexto(ILojaFacade facade, StackPane conteudoPai) {
        this.facade = facade;
        this.conteudoPai = conteudoPai;
        carregarCategoriasEFornecedores();
    }

    private void carregarCategoriasEFornecedores() {
        Map<String, Categoria> mapCategorias = facade.listarCategoria();
        if (mapCategorias != null) {
            listaCategoria.setItems(FXCollections.observableArrayList(mapCategorias.values()));
        }

        Map<String, Fornecedor> mapaFornecedores = facade.listarFornecedor();
        if (mapaFornecedores != null) {
            listaFornecedor.setItems(FXCollections.observableArrayList(mapaFornecedores.values()));
        }
    }

    @FXML
    void cancelar(ActionEvent event) {
        voltarParaGerenciarItens();
    }

    @FXML
    void registrar(ActionEvent event) {
        String id = campoID.getText();
        String nome = campoNome.getText();
        String taxaDiaria = campoTaxaDiaria.getText();
        String valorReposicao = campoValorReposicao.getText();
        Categoria categoria = listaCategoria.getValue();
        Fornecedor fornecedor = listaFornecedor.getValue();

        try {
            BigDecimal taxaDiariaB = converterParaBigDecimal(taxaDiaria);
            BigDecimal valorReposicaoB = converterParaBigDecimal(valorReposicao);

            if (id == null || id.isBlank() || nome == null || nome.isBlank() || categoria == null || fornecedor == null) {
                Alerta.exibirAlertaAviso("Por favor, preencha todos os campos obrigatórios.");
                return;
            }

            if (taxaDiariaB.compareTo(BigDecimal.ZERO) <= 0 || valorReposicaoB.compareTo(BigDecimal.ZERO) <= 0) {
                Alerta.exibirAlertaAviso("Insira um valor válido e/ou maior que zero!");
                return;
            }

            Item item = new Item(id.toUpperCase(), nome.toUpperCase(), taxaDiariaB, valorReposicaoB, "DISPONIVEL", categoria, fornecedor);
            facade.cadastrarItem(item);

            Alerta.exibirAlertaSucesso("Sucesso!", "Item cadastrado com sucesso!");

            voltarParaGerenciarItens();

        } catch (BusinessException e) {
            Alerta.exibirAlertaAviso("Atenção: " + e.getMessage());
        }
    }

    private void voltarParaGerenciarItens() {
        try {
            java.net.URL url = getClass().getResource("/ui/admin/gerenciar-itens.fxml");
            if (url == null) {
                Alerta.exibirAlertaErro("Erro", "Arquivo gerenciar-itens.fxml não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            GerenciarItensController controller = loader.getController();
            controller.setContexto(this.facade, this.conteudoPai);

            this.conteudoPai.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de carregamento", "Não foi possível carregar a tela de gerenciar itens.");
        }
    }

    private BigDecimal converterParaBigDecimal(String texto) {
        if (texto == null || texto.isBlank()) {
            return BigDecimal.ZERO;
        }

        String limpo = texto.trim();

        limpo = limpo.replaceFirst("^(?i)(R\\$|RS|\\$)\\s*", "").trim();

        boolean formatoValido = limpo.matches("^\\d{1,3}(\\.\\d{3})*(,\\d{1,2})?$") || limpo.matches("^\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?$") || limpo.matches("^\\d+([.,]\\d{1,2})?$");

        if (!formatoValido) {
            return BigDecimal.ZERO;
        }

        if (limpo.contains(",") && limpo.contains(".")) {
            if (limpo.lastIndexOf(',') > limpo.lastIndexOf('.')) {
                limpo = limpo.replace(".", "").replace(",", ".");
            } else {
                limpo = limpo.replace(",", "");
            }
        } else if (limpo.contains(",")) {
            limpo = limpo.replace(",", ".");
        }

        try {
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}