package com.loja.controllers.admin;

import com.loja.controllers.Alerta;
import com.loja.model.Fornecedor;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NvForneAdminController {

    @FXML
    private Label labelTitulo;

    @FXML
    private TextField cadastroCnpj;

    @FXML
    private TextField cadastroID;

    @FXML
    private TextField cadastroNome;

    @FXML
    private TextField cadastroTel;

    @FXML
    private Button botaoCancelar;

    @FXML
    private Button botaoSalvarForne;

    private ILojaFacade facade;
    private Fornecedor fornecedor;

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;

        if (fornecedor == null) {
            // Modo NOVO
            labelTitulo.setText("Novo Fornecedor");
            botaoSalvarForne.setText("Cadastrar");
            cadastroID.setDisable(false);
            return;
        }

        // Modo EDITAR
        labelTitulo.setText("Editar Fornecedor");
        botaoSalvarForne.setText("Salvar");

        cadastroID.setText(fornecedor.getId());
        cadastroID.setDisable(true); // ID não pode ser alterado
        cadastroNome.setText(fornecedor.getNome());
        cadastroCnpj.setText(fornecedor.getCnpj());
        cadastroTel.setText(fornecedor.getTelefone());
    }

    @FXML
    void salvar(ActionEvent event) {
        String id = cadastroID.getText().trim();
        String nome = cadastroNome.getText().trim();
        String cnpj = cadastroCnpj.getText().trim();
        String telefone = cadastroTel.getText().trim();

        if (fornecedor == null && id.isEmpty()) {
            Alerta.exibirAlertaAviso("Preencha o ID.");
            return;
        }
        if (nome.isEmpty() || cnpj.isEmpty() || telefone.isEmpty()) {
            Alerta.exibirAlertaAviso("Preencha nome, CNPJ e telefone.");
            return;
        }
        try {
            if (fornecedor == null) {
                facade.cadastrarFornecedor(new Fornecedor(id, nome, cnpj, telefone));
                Alerta.exibirAlertaSucesso("Fornecedor criado!");
            } else {
                fornecedor.setNome(nome);
                fornecedor.setCnpj(cnpj);
                fornecedor.setTelefone(telefone);
                facade.atualizarFornecedor(fornecedor);
                Alerta.exibirAlertaSucesso("Fornecedor atualizado!");
            }
            fechar();
        } catch (RuntimeException e) {
            Alerta.exibirAlertaErro("Erro ao salvar fornecedor", e.getMessage());
        }
    }

    @FXML
    void cancelar(ActionEvent event) {
        fechar();
    }

    private void fechar() {
        ((Stage) cadastroNome.getScene().getWindow()).close();
    }
}