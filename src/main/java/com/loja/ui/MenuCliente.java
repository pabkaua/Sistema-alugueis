package com.loja.ui;

import com.loja.padraofacade.interfaces.ILojaFacade;
import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.model.Multa;

import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuCliente {

    private static final Logger logger = Logger.getLogger(MenuCliente.class.getName());

    private final ILojaFacade facade;
    private final Cliente usuarioLogado;
    private final Scanner scanner;

    public MenuCliente(ILojaFacade facade, Cliente usuarioLogado, Scanner scanner) {
        this.facade = facade;
        this.usuarioLogado = usuarioLogado;
        this.scanner = scanner;
    }

    public void exibir() {
        boolean logado = true;

        while (logado) {

            if (logger.isLoggable(Level.INFO)) {
                String nomeCliente = usuarioLogado.getNome().toUpperCase();

                logger.log(
                        Level.INFO,
                        "\nÁREA DO CLIENTE: {0}",
                        nomeCliente
                );
            }

            logger.info("1 - Verificar Itens Disponíveis para Aluguel");
            logger.info("2 - Ver Meus Aluguéis (Histórico)");
            logger.info("3 - Verificar Minhas Multas Pendentes");
            logger.info("0 - Sair");
            logger.info("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> verItensDisponiveis();
                case "2" -> verMeusAlugueis();
                case "3" -> verMultasPendentes();
                case "0" -> {
                    logger.info("Saindo...");
                    logado = false;
                }
                default -> logger.info("Opção inválida!");
            }
        }
    }

    public void verItensDisponiveis() {
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

    public void verMeusAlugueis() {
        logger.info("\nHISTÓRICO DE ALUGUÉIS");

        try {
            Map<String, ContratoAluguel> contratos =
                    facade.consultarHistoricoCliente(usuarioLogado.getId());

            if (contratos.isEmpty()) {
                logger.info(
                        "Você não possui registros de aluguéis."
                );
            } else {
                for (ContratoAluguel c : contratos.values()) {

                    String itemNome;

                    if (c.getItem() != null) {
                        itemNome = c.getItem().getNome();
                    } else {
                        itemNome = "Item não identificado";
                    }

                    logger.log(
                            Level.INFO,
                            "Contrato ID: {0} | Item: {1} | Retirada: {2} | Prev. Devolução: {3}",
                            new Object[]{
                                    c.getId(),
                                    itemNome,
                                    c.getDataRetirada(),
                                    c.getDataPrevDevolucao()
                            }
                    );
                }
            }

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao buscar histórico: {0}",
                    e.getMessage()
            );
        }
    }

    public void verMultasPendentes() {
        logger.info("\nMINHAS MULTAS");

        try {
            boolean temMulta =
                    facade.possuiMultaPendente(usuarioLogado.getId());

            if (!temMulta) {
                logger.info(
                        "Você não possui multas pendentes no momento."
                );
            } else {
                Map<String, Multa> multas =
                        facade.listarMultaPorCliente(usuarioLogado.getId());

                for (Multa m : multas.values()) {
                    logger.log(
                            Level.INFO,
                            "Multa ID: {0} | Motivo: {1} | Total: R$ {2} | Status: {3}",
                            new Object[]{
                                    m.getId(),
                                    m.getMotivo(),
                                    m.getValorTotal(),
                                    m.getStatus()
                            }
                    );
                }
            }

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao processar verificação de multas: {0}",
                    e.getMessage()
            );
        }
    }
}