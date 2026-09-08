package com.loja.ui;

import com.loja.padraoFacade.interfaces.ILojaFacade;
import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.model.Multa;
import java.util.Map;
import java.util.Scanner;

public class MenuCliente {

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
            System.out.println("\nÁREA DO CLIENTE: " + usuarioLogado.getNome().toUpperCase());
            System.out.println("1 - Verificar Itens Disponíveis para Aluguel");
            System.out.println("2 - Ver Meus Aluguéis (Histórico)");
            System.out.println("3 - Verificar Minhas Multas Pendentes");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> verItensDisponiveis();
                case "2" -> verMeusAlugueis();
                case "3" -> verMultasPendentes();
                case "0" -> {
                    System.out.println("Saindo...");
                    logado = false;
                }
                default -> System.out.println("Opção inválida!");
            }

        }
    }

    public void verItensDisponiveis() {
        System.out.println("\nITENS DISPONÍVEIS");
        try {
            Map<String, Item> itens = facade.listarItensDisponiveis();
            if (itens.isEmpty()) {
                System.out.println("Não há itens disponíveis para aluguel no momento.");
            } else {
                for (Item item : itens.values()) {
                    System.out.println("ID: " + item.getId() + " | Nome: " + item.getNome() + " | Valor Diário: " + item.getTaxaDiaria());
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao listar itens: " + e.getMessage());
        }
    }

    public void verMeusAlugueis() {
        System.out.println("\nHISTÓRICO DE ALUGUÉIS");
        try {
            Map<String, ContratoAluguel> contratos = facade.consultarHistoricoCliente(usuarioLogado.getId());
            if (contratos.isEmpty()) {
                System.out.println("Você não possui registros de aluguéis.");
            } else {
                for (ContratoAluguel c : contratos.values()) {
                    String itemNome = (c.getItem() != null) ? c.getItem().getNome() : "Item não identificado";
                    System.out.println("Contrato ID: " + c.getId() + " | Item: " + itemNome + " | Retirada: " + c.getDataRetirada() + " | Prev. Devolução: " + c.getDataPrevDevolucao());
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao buscar histórico: " + e.getMessage());
        }
    }

    public void verMultasPendentes() {
        System.out.println("\nMINHAS MULTAS");
        try {
            boolean temMulta = facade.possuiMultaPendente(usuarioLogado.getId());
            if (!temMulta) {
                System.out.println("Você não possui multas pendentes no momento.");
            } else {
                Map<String, Multa> multas = facade.listarMultaPorCliente(usuarioLogado.getId());
                for (Multa m : multas.values()) {
                    System.out.println("Multa ID: " + m.getId() + " | Motivo: " + m.getMotivo() + " | Total: R$ " + m.getValorTotal() + " | Status: " + m.getStatus());
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao processar verificação de multas: " + e.getMessage());
        }
    }
}