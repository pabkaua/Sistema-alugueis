package com.loja.controllers;

import com.loja.controllers.funcionario.*;
import com.loja.padraofacade.interfaces.ILojaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SidebarFuncionarioController {

    @FXML private Button botaoDashboard;
    @FXML private Button botaoLogout;
    @FXML private Button btnFuncAlugueis;
    @FXML private Button btnFuncCadastrarCliente;
    @FXML private Button btnFuncContratosAtivos;
    @FXML private Button btnFuncContratosPorClientes;
    @FXML private Button btnFuncGerenciarClientes;
    @FXML private Button btnFuncItensDisponiveis;
    @FXML private Button btnFuncQuitarMulta;
    @FXML private Button btnFuncRegistrarAluguel;
    @FXML private Button btnFuncRelatorios;
    @FXML private StackPane conteudo;
    @FXML private VBox sidebarVbox;
    @FXML private Label tituloLoja;
    @FXML private VBox submenuAlugueisFunc;
    @FXML private VBox submenuGerenciarClientesFunc;
    @FXML private VBox submenuRelatoriosFunc;

    private ILojaFacade facade;

    // funções da classe
    public void setContexto(ILojaFacade facade) {
        this.facade = facade;
    }

    // funções do fxml
    @FXML
    public void initialize() {
        submenuAlugueisFunc.managedProperty().bind(submenuAlugueisFunc.visibleProperty());
        submenuGerenciarClientesFunc.managedProperty().bind(submenuGerenciarClientesFunc.visibleProperty());
        submenuRelatoriosFunc.managedProperty().bind(submenuRelatoriosFunc.visibleProperty());
    }

    @FXML
    void alternarAlugueis(ActionEvent event) {
        boolean visivel = !submenuAlugueisFunc.isVisible();
        submenuAlugueisFunc.setVisible(visivel);
        btnFuncAlugueis.setText(visivel ? "⌃   Alugueis" : "⌄   Alugueis");
    }

    @FXML
    void alternarGerenciarClientes(ActionEvent event) {
        boolean visivel = !submenuGerenciarClientesFunc.isVisible();
        submenuGerenciarClientesFunc.setVisible(visivel);
        btnFuncGerenciarClientes.setText(visivel ? "⌃   Gerenciar Clientes" : "⌄   Gerenciar Clientes");
    }

    @FXML
    void alternarRelatorios(ActionEvent event) {
        boolean visivel = !submenuRelatoriosFunc.isVisible();
        submenuRelatoriosFunc.setVisible(visivel);
        btnFuncRelatorios.setText(visivel ? "⌃   Relatórios" : "⌄   Relatórios");
    }

    // funções de navegação
    @FXML
    void navegarDashboard(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/funcionario/dashboard.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo dashboard.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void navegarItensDisponiveis(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/funcionario/itens-disponiveis.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo itens-disponiveis.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            ItensDisponiveisController controller = loader.getController();
            controller.setDados(facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void navegarContratosAtivos(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/funcionario/contratos-ativos-funcionario.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo contratos-ativos-funcionario.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            ContratosAtivosFuncionarioController controller = loader.getController();
            controller.setDados(facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void navegarContratosPorCliente(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/funcionario/contratos-por-cliente.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo contratos-por-cliente.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            ContratosPorClienteController controller = loader.getController();
            controller.setDados(facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void cadastrarCliente(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/funcionario/cadastrar-cliente-funcionario.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo cadastrar-cliente-funcionario.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            CadastrarClienteFuncionarioController controller = loader.getController();
            controller.setDados(facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void quitarMulta(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/funcionario/quitar-multa-funcionario.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo quitar-multa-funcionario.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            QuitarMultaFuncionarioController controller = loader.getController();
            controller.setDados(facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void processarDevolucao(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/funcionario/devolucao-funcionario.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo devolucao-funcionario.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            DevolucaoFuncionarioController controller = loader.getController();
            controller.setDados(facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void registrarAluguel(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/funcionario/registrar-aluguel-funcionario.fxml");

            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo registrar-aluguel-funcionario.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node tela = loader.load();

            RegistrarAluguelFuncionarioController controller = loader.getController();
            controller.setDados(facade);

            conteudo.getChildren().setAll(tela);
        } catch (IOException e) {
            Alerta.exibirAlertaErro("Erro de navegação", e.getMessage());
        }
    }

    @FXML
    void fazerLogout(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ui/LoginScreen.fxml");
            if (url == null) {
                Alerta.exibirAlertaErro("Erro de navegação", "O arquivo LoginScreen.fxml não foi localizado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent telaLogin = loader.load();

            LoginController controller = loader.getController();
            controller.setFacade(this.facade);

            Node botaoLogoutClicado = (Node) event.getSource();
            Stage stage = (Stage) botaoLogoutClicado.getScene().getWindow();

            stage.setTitle("Login - Aluga Tudo");

            if (stage.getScene() != null) {
                stage.getScene().setRoot(telaLogin);
            } else {
                stage.setScene(new Scene(telaLogin));
            }

        } catch (IOException e) {
            Alerta.exibirAlertaErro("Falha ao abrir a tela", "Houve um erro ao carregar a tela.");
        }
    }
}
