package com.loja.controllers.admin;

import com.loja.controllers.Alerta;
import com.loja.model.Fornecedor;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NvForneAdminController {

    @FXML
    private TextField CadastroCnpj;

    @FXML
    private TextField CadastroID;

    @FXML
    private TextField CadastroNome;

    @FXML
    private TextField CadastroTel;

    @FXML
    private Button botaoCancelar;

    @FXML
    private Button botaoSalvarForne;

    @FXML
    private Label labelTitulo;

    private ILojaFacade facade;
    private Fornecedor fornecedor;

    public void setFacade(ILojaFacade facade) {
        this.facade = facade;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
        if (fornecedor == null) {
            labelTitulo.setText("Novo Fornecedor");
            botaoSalvarForne.setText("Cadastrar");
            CadastroID.setDisable(false);
            return;
        }
        labelTitulo.setText("Editar Fornecedor");
        botaoSalvarForne.setText("Salvar");

        CadastroID.setText(fornecedor.getId());
        CadastroID.setDisable(true);
        CadastroNome.setText(fornecedor.getNome());
        CadastroCnpj.setText(fornecedor.getCnpj());
        CadastroTel.setText(fornecedor.getTelefone());
    }


    @FXML
    void salvar(ActionEvent event) {
        String id = CadastroID.getText().trim();
        String nome = CadastroNome.getText().trim();
        String cnpj = CadastroCnpj.getText().trim();
        String telefone = CadastroTel.getText().trim();

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
        ((Stage) CadastroNome.getScene().getWindow()).close();
    }
}

