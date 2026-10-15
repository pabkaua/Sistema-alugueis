package com.loja.ui;

import com.loja.model.Item;
import com.loja.padraofacade.interfaces.ILojaFacade;
import com.loja.model.Funcionario;
import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuFuncionario {

    private static final Logger logger = Logger.getLogger(MenuFuncionario.class.getName());

    private final ILojaFacade facade;
    private final Funcionario usuarioLogado;
    private final Scanner scanner;

    public MenuFuncionario(ILojaFacade facade, Funcionario usuarioLogado, Scanner scanner) {
        this.facade = facade;
        this.usuarioLogado = usuarioLogado;
        this.scanner = scanner;
    }

    public void exibir() {
        boolean ativo = true;

        while (ativo) {

            if (logger.isLoggable(Level.INFO)) {
                String nomeFuncionario = usuarioLogado.getNome().toUpperCase();

                logger.log(
                        Level.INFO,
                        "\nPAINEL DO FUNCIONÁRIO: {0}",
                        nomeFuncionario
                );
            }

            logger.info("1 - Registrar Novo Aluguel");
            logger.info("2 - Processar Devolução de Item");
            logger.info("3 - Cadastrar Novo Cliente");
            logger.info("4 - Emitir Relatórios de itens disponíveis");
            logger.info("5 - Emitir Relatórios de contratos ativos");
            logger.info("6 - Emitir Relatórios de contratos por cliente");
            logger.info("7 - Quitar uma Multa de Cliente");
            logger.info("0 - Sair");
            logger.info("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> registrarAluguel();
                case "2" -> processarDevolucao();
                case "3" -> cadastrarCliente();
                case "4" -> emitirRelatorioDisponiveis();
                case "5" -> emitirRelatorioAlugados();
                case "6" -> emitirRelatorioContratosCliente();
                case "7" -> quitarMulta();
                case "0" -> {
                    logger.info("Saindo...");
                    ativo = false;
                }
                default -> logger.info("Opção inválida!");
            }
        }
    }

    private void registrarAluguel() {
        logger.info("\nREGISTRAR NOVO ALUGUEL");

        logger.info("ID do Cliente: ");
        String clienteId = scanner.nextLine();

        logger.info("ID do Item: ");
        String itemId = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataRetirada = null;
        LocalDate dataPrevDevolucao = null;

        while (dataRetirada == null) {
            try {
                logger.info("Data de Retirada (dd/MM/yyyy): ");
                dataRetirada = LocalDate.parse(scanner.nextLine(), formatter);
            } catch (DateTimeParseException e) {
                logger.info("Data inválida, tente novamente.");
            }
        }

        while (dataPrevDevolucao == null) {
            try {
                logger.info("Data Prevista de Devolução (dd/MM/yyyy): ");
                dataPrevDevolucao = LocalDate.parse(scanner.nextLine(), formatter);

                if (dataPrevDevolucao.isBefore(dataRetirada)) {
                    logger.info(
                            "Data de devolução não pode ser anterior à data de retirada."
                    );
                    dataPrevDevolucao = null;
                }

            } catch (DateTimeParseException e) {
                logger.info("Data inválida, tente novamente.");
            }
        }

        try {
            ContratoAluguel contrato = facade.registrarAluguel(
                    clienteId,
                    itemId,
                    dataRetirada,
                    dataPrevDevolucao
            );

            logger.log(
                    Level.INFO,
                    "Sucesso! Contrato firmado com o ID: {0}",
                    contrato.getId()
            );

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao abrir aluguel: {0}",
                    e.getMessage()
            );
        }
    }

    private void processarDevolucao() {
        logger.info("\nPROCESSAR DEVOLUÇÃO");
        logger.info("Digite o ID do Contrato de Aluguel: ");

        String contratoId = scanner.nextLine();

        try {
            ContratoAluguel contrato = facade.processarDevolucao(contratoId);

            logger.log(
                    Level.INFO,
                    "Devolução processada com sucesso! Contrato ID: {0} finalizado.",
                    contrato.getId()
            );

            if (facade.possuiMultaPendente(contrato.getCliente().getId())) {
                logger.info(
                        "Atenção: devolução em atraso, multa aplicada ao cliente."
                );
            }

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao processar encerramento de contrato: {0}",
                    e.getMessage()
            );
        }
    }

    private void cadastrarCliente() {
        logger.info("\nCADASTRO DE NOVO CLIENTE");

        logger.info("ID: ");
        String id = scanner.nextLine();

        logger.info("Nome Completo: ");
        String nome = scanner.nextLine();

        logger.info("E-mail: ");
        String email = scanner.nextLine();

        logger.info("Senha de Acesso: ");
        String senha = scanner.nextLine();

        Cliente novoCliente = new Cliente(id, nome, email, senha);

        try {
            facade.cadastrarCliente(novoCliente);
            logger.info("Cliente cadastrado com sucesso!");

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Falha ao salvar cliente: {0}",
                    e.getMessage()
            );
        }
    }

    private void emitirRelatorioDisponiveis() {
        logger.info("\nITENS DISPONÍVEIS");

        try {
            Map<String, Item> itens = facade.listarItensDisponiveis();

            if (itens.isEmpty()) {
                logger.info(
                        "Não há itens disponíveis para aluguel no momento."
                );

            } else {
                for (Item item : itens.values()) {
                    logger.log(
                            Level.INFO,
                            "ID: {0} | Nome: {1} | Valor Diário: {2}",
                            new Object[]{
                                    item.getId(),
                                    item.getNome(),
                                    item.getTaxaDiaria()
                            }
                    );
                }
            }

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao listar itens: {0}",
                    e.getMessage()
            );
        }
    }

    private void emitirRelatorioAlugados() {
        logger.info("\nRELATÓRIO DE CONTRATOS ATIVOS");

        try {
            String relatorio = facade.gerarRelatorioItensAlugados();
            logger.info(relatorio);

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao gerar relatório: {0}",
                    e.getMessage()
            );
        }
    }

    private void emitirRelatorioContratosCliente() {
        logger.info("\nRELATÓRIO DE CONTRATOS POR CLIENTE");
        logger.info("Digite o ID do cliente: ");

        String id = scanner.nextLine();

        try {
            Map<String, ContratoAluguel> contratos =
                    facade.consultarHistoricoCliente(id);

            if (contratos.isEmpty()) {
                logger.info(
                        "Não há histórico de contratos para esse cliente."
                );

            } else {
                for (ContratoAluguel con : contratos.values()) {
                    logger.log(
                            Level.INFO,
                            "ID: {0} | Item: {1} | Valor total: {2} | Status: {3} | Devolução prevista: {4}",
                            new Object[]{
                                    con.getId(),
                                    con.getItem().getNome(),
                                    con.getValorTotal(),
                                    con.getStatus(),
                                    con.getDataPrevDevolucao()
                            }
                    );
                }
            }

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao listar contratos: {0}",
                    e.getMessage()
            );
        }
    }

    private void quitarMulta() {
        logger.info("\nQUITAR MULTA FINANCEIRA");
        logger.info("Digite o ID da Multa a ser quitada: ");

        String multaId = scanner.nextLine();

        try {
            facade.quitarMulta(multaId);
            logger.info("Sucesso! A multa foi alterada para QUITADA.");

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao dar baixa na multa: {0}",
                    e.getMessage()
            );
        }
    }
}