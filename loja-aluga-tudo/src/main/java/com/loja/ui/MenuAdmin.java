package com.loja.ui;

import com.loja.model.*;
import com.loja.padraoFacade.interfaces.ILojaFacade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;

public class MenuAdmin {

    private final ILojaFacade facade;
    private final Administrador usuarioLogado;
    private final Scanner scanner;

    public MenuAdmin(ILojaFacade facade, Administrador usuarioLogado, Scanner scanner) {
        this.facade = facade;
        this.usuarioLogado = usuarioLogado;
        this.scanner = scanner;
    }

    public void exibir() {
        boolean ativo = true;
        while (ativo) {
            System.out.println("\n=== PAINEL ADMINISTRATIVO: " + usuarioLogado.getNome().toUpperCase() + " ===");
            System.out.println("1 - Gerenciar Usuários");
            System.out.println("2 - Gerenciar Itens");
            System.out.println("3 - Gerenciar Categorias");
            System.out.println("4 - Gerenciar Fornecedores");
            System.out.println("5 - Emitir Relatórios");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> gerenciarUsuarios();
                case "2" -> gerenciarItens();
                case "3" -> gerenciarCategorias();
                case "4" -> gerenciarFornecedores();
                case "5" -> emitirRelatorios();
                case "0" -> {
                    System.out.println("Saindo do painel administrativo...");
                    ativo = false;
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void gerenciarUsuarios() {
        System.out.println("\nGERENCIAR USUÁRIOS");
        System.out.println("1 - Cadastrar Usuário (Cliente/Func/Adm)");
        System.out.println("2 - Listar Usuários");
        System.out.println("3 - Atualizar Usuário");
        System.out.println("4 - Desativar Usuário");
        System.out.print("Escolha uma opção: ");
        String subOpcao = scanner.nextLine();

        try {
            if (subOpcao.equals("1")) { // cadastrar
                System.out.println("Tipo: 1-Cliente | 2-Funcionário | 3-Administrador");
                System.out.print("Escolha o tipo: ");
                String tipo = scanner.nextLine();

                System.out.print("ID: ");
                String id = scanner.nextLine();

                System.out.print("Nome: ");
                String nome = scanner.nextLine();

                System.out.print("Email/Login: ");
                String email = scanner.nextLine();

                System.out.print("Senha: ");
                String senha = scanner.nextLine();

                if (tipo.equals("1")) {
                    facade.cadastrarCliente(new Cliente(id, nome, email, senha));
                    System.out.println("Cliente cadastrado com sucesso!");
                } else if (tipo.equals("2")) {
                    System.out.print("Cargo do Funcionário: ");
                    String cargo = scanner.nextLine();
                    facade.cadastrarFuncionario(new Funcionario(id, nome, email, senha, cargo));
                    System.out.println("Funcionário cadastrado com sucesso!");
                } else if (tipo.equals("3")) {
                    facade.cadastrarAdm(new Administrador(id, nome, email, senha));
                    System.out.println("Administrador cadastrado com sucesso!");
                } else {
                    System.out.println("Tipo de usuário inválido!");
                }

            }
            else if (subOpcao.equals("2")) { // listar
                System.out.println("1-Todos | 2-Por Perfil (CLIENTE/FUNCIONARIO/ADMINISTRADOR)");
                System.out.print("Opção: ");
                String listOpt = scanner.nextLine();

                if (listOpt.equals("1")) {
                    facade.listarUsuario().values().forEach(u -> System.out.println("ID: " + u.getId() + " | Nome: " + u.getNome() + " | Perfil: " + u.getPerfil()));
                } else if (listOpt.equals("2")) {
                    System.out.print("Perfil desejado: ");
                    String perfil = scanner.nextLine().toUpperCase();
                    if(facade.listarUsuarioPorPerfil(perfil).isEmpty()) throw new RuntimeException("Nehum usuário de perfil " + perfil);
                    facade.listarUsuarioPorPerfil(perfil).values().forEach(u -> System.out.println("ID: " + u.getId() + " | Nome: " + u.getNome()));
                } else {
                    System.out.println("Digite uma opção válida!");
                }

            }
            else if (subOpcao.equals("3")) { // atualizar
                System.out.print("ID do usuário a atualizar: ");
                String id = scanner.nextLine();

                Usuario u = facade.buscarUsuario(id);

                System.out.println("O que você deseja atualizar?");
                System.out.println("1 - Nome");
                System.out.println("2 - Email/Login");
                System.out.println("3 - Senha");
                System.out.println("4 - Cargo (quando aplicavel)");
                String escolha = scanner.nextLine();

                if (escolha.equals("1")) {
                    System.out.print("Novo Nome (" + u.getNome() + "): ");
                    String novoNome = scanner.nextLine();
                    if (novoNome.isBlank()) throw new RuntimeException("nome inválido!");
                    u.setNome(novoNome);
                } else if (escolha.equals("2")) {
                    System.out.print("Novo Email/Login (" + u.getLogin() + "): ");
                    String novoLogin = scanner.nextLine();
                    if (novoLogin.isBlank()) throw new RuntimeException("login inválido!");
                    u.setLogin(novoLogin);
                } else if (escolha.equals("3")) {
                    System.out.print("Nova senha: ");
                    String novaSenha = scanner.nextLine();
                    if (novaSenha.isBlank() || novaSenha.length() < 3) throw new RuntimeException("Senha inválida!");
                    u.setSenha(novaSenha);
                } else if (escolha.equals("4") && !(u instanceof Funcionario)) {
                    throw new RuntimeException("O usuário não é funcionário!");
                } else if (escolha.equals("4")) {
                    System.out.print("Novo cargo (" + ((Funcionario) u).getCargo() + "): ");
                    String novoCargo = scanner.nextLine();
                    if (novoCargo.isBlank()) throw new RuntimeException("Cargo inválido!");
                    ((Funcionario) u).setCargo(novoCargo);
                } else {
                    throw new RuntimeException("Opção inválida!");
                }

                facade.atualizarUsuario(id, u);
                System.out.println("Usuário atualizado com sucesso!");

            }
            else if (subOpcao.equals("4")) { // desativar
                System.out.print("ID do usuário a desativar: ");
                String id = scanner.nextLine();
                facade.desativarUsuario(id);
                System.out.println("Usuário desativado com sucesso.");
            }

        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void gerenciarItens() {
        System.out.println("\nGERENCIAR ITENS");
        System.out.println("1 - Cadastrar Item");
        System.out.println("2 - Listar Itens");
        System.out.println("3 - Atualizar Item");
        System.out.println("4 - Deletar Item");
        System.out.print("Escolha uma opção: ");
        String subOpcao = scanner.nextLine();

        try {
            if (subOpcao.equals("1")) {
                Item item = new Item();

                System.out.print("ID: ");
                item.setId(scanner.nextLine());

                System.out.print("Nome: ");
                item.setNome(scanner.nextLine());

                item.setStatus("DISPONIVEL");

                System.out.print("ID Categoria: ");
                item.setCategoria(facade.buscarCategoria(scanner.nextLine()));

                System.out.print("ID Fornecedor: ");
                item.setFornecedor(facade.buscarFornecedor(scanner.nextLine()));

                try {
                    System.out.print("Taxa Diária (XX.xx):  R$ ");
                    BigDecimal novovalor = new BigDecimal(scanner.nextLine());
                    if (novovalor.compareTo(BigDecimal.ZERO) < 0) {
                        throw new RuntimeException("O valor não pode ser negativo!");
                    }
                    item.setTaxaDiaria(novovalor);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Valor inválido para taxa diária.");
                }

                try {
                    System.out.print("Valor de reposição (XX.xx): R$ ");
                    BigDecimal novovalor = new BigDecimal(scanner.nextLine());
                    if (novovalor.compareTo(BigDecimal.ZERO) < 0) {
                        throw new RuntimeException("O valor não pode ser negativo!");
                    }
                    item.setValorReposicao(novovalor);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Valor inválido para o valor de reposição.");
                }

                facade.cadastrarItem(item);
                System.out.println("Item cadastrado!");

            }
            else if (subOpcao.equals("2")) {
                System.out.println("1-Todos | 2-Por Status | 3-Por Categoria | 4-Por Fornecedor");
                System.out.print("Opção: ");
                String opt = scanner.nextLine();

                if (opt.equals("1")) {
                    facade.listarItem().values().forEach(i -> System.out.println("ID: " + i.getId() + " | Nome: " + i.getNome() + " | Status: " + i.getStatus()));
                } else if (opt.equals("2")) {
                    System.out.print("Status (DISPONIVEL/ALUGADO): ");
                    String status = scanner.nextLine().toUpperCase();
                    facade.listarItemPorStatus(status).values().forEach(i -> System.out.println("ID: " + i.getId() + " | Nome: " + i.getNome()));
                } else if (opt.equals("3")) {
                    System.out.print("ID Categoria: ");
                    Categoria cat = facade.buscarCategoria(scanner.nextLine());
                    facade.listarItemPorCategoria(cat).values().forEach(i -> System.out.println("ID: " + i.getId() + " | Nome: " + i.getNome()));
                } else if (opt.equals("4")) {
                    System.out.print("ID Fornecedor: ");
                    Fornecedor forn = facade.buscarFornecedor(scanner.nextLine());
                    facade.listarItemPorFornecedor(forn).values().forEach(i -> System.out.println("ID: " + i.getId() + " | Nome: " + i.getNome()));
                }

            }
            else if (subOpcao.equals("3")) {
                System.out.print("ID do Item: ");
                Item item = facade.buscarItem(scanner.nextLine());

                System.out.println("O que você deseja atualizar?");
                System.out.println("1 - Nome");
                System.out.println("2 - Taxa diária");
                System.out.println("3 - Valor de reposição");
                System.out.println("4 - Categoria");
                System.out.println("5 - Fornecedor");
                String escolha = scanner.nextLine();

                if (escolha.equals("1")) {
                    System.out.print("Novo Nome (" + item.getNome() + "): ");
                    String novoNome = scanner.nextLine();
                    if (novoNome.isBlank()) throw new RuntimeException("nome inválido!");
                    item.setNome(novoNome);
                } else if (escolha.equals("2")) {
                    try {
                        System.out.print("Nova taxa diária (" + item.getTaxaDiaria() + ")(XX.xx):  R$ ");
                        BigDecimal novovalor = new BigDecimal(scanner.nextLine());
                        if (novovalor.compareTo(BigDecimal.ZERO) < 0) {
                            throw new RuntimeException("O valor não pode ser negativo!");
                        }
                        item.setTaxaDiaria(novovalor);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Valor inválido para taxa diária.");
                    }
                } else if (escolha.equals("3")) {
                    try {
                        System.out.print("Valor de reposição (XX.xx): R$ ");
                        BigDecimal novovalor = new BigDecimal(scanner.nextLine());
                        if (novovalor.compareTo(BigDecimal.ZERO) < 0) {
                            throw new RuntimeException("O valor não pode ser negativo!");
                        }
                        item.setValorReposicao(novovalor);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Valor inválido para o valor de reposição.");
                    }
                } else if (escolha.equals("4")) {
                    System.out.print("Digite o id da categoria (" + item.getCategoria().getId() + "): ");
                    String novaCategoriaId = scanner.nextLine();
                    item.setCategoria(facade.buscarCategoria(novaCategoriaId));
                } else if (escolha.equals("5")) {
                    System.out.print("Digite o id do fornecedor (" + item.getFornecedor().getId() + "): ");
                    String novoFornecedorId = scanner.nextLine();
                    item.setFornecedor(facade.buscarFornecedor(novoFornecedorId));
                } else {
                    throw new RuntimeException("Opção inválida!");
                }

                facade.atualizarItem(item);
                System.out.println("Item atualizado com sucesso!");

            }
            else if (subOpcao.equals("4")) {
                System.out.print("ID do Item a deletar: ");
                facade.deletarItem(scanner.nextLine());
                System.out.println("Item deletado do repositório.");
            }
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void gerenciarCategorias() {
        System.out.println("\nGERENCIAR CATEGORIAS");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar");
        System.out.println("3 - Atualizar");
        System.out.println("4 - Deletar");
        System.out.print("Opção: ");
        String subOpcao = scanner.nextLine();

        try {
            if (subOpcao.equals("1")) {
                System.out.print("ID: ");
                String id = scanner.nextLine();
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                facade.cadastrarCategoria(new Categoria(id, nome));
                System.out.println("Categoria criada!");

            } else if (subOpcao.equals("2")) {
                facade.listarCategoria().values().forEach(c -> System.out.println("ID: " + c.getId() + " | Nome: " + c.getNome()));

            } else if (subOpcao.equals("3")) {
                System.out.print("ID: ");
                Categoria c = facade.buscarCategoria(scanner.nextLine());
                System.out.print("Novo Nome: ");
                c.setNome(scanner.nextLine());
                facade.atualizarCategoria(c);
                System.out.println("Categoria atualizada!");

            } else if (subOpcao.equals("4")) {
                System.out.print("ID a deletar: ");
                facade.deletarCategoria(scanner.nextLine());
                System.out.println("Categoria removida.");
            }
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void gerenciarFornecedores() {
        System.out.println("\nGERENCIAR FORNECEDORES");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Listar");
        System.out.println("3 - Atualizar");
        System.out.println("4 - Deletar");
        System.out.print("Opção: ");
        String subOpcao = scanner.nextLine();

        try {
            if (subOpcao.equals("1")) {
                System.out.print("ID: ");
                String id = scanner.nextLine();
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                System.out.print("CNPJ: ");
                String cnpj = scanner.nextLine();
                System.out.print("Telefone: ");
                String telefone = scanner.nextLine();

                facade.cadastrarFornecedor(new Fornecedor(id, nome, cnpj, telefone));
                System.out.println("Fornecedor criado!");
            } else if (subOpcao.equals("2")) {
                facade.listarFornecedor().values().forEach(f -> System.out.println("ID: " + f.getId() + " | Nome: " + f.getNome() + " | CNPJ: " + f.getCnpj() + " | Telefone: " + f.getTelefone()));

            } else if (subOpcao.equals("3")) {
                System.out.print("ID: ");
                Fornecedor f = facade.buscarFornecedor(scanner.nextLine());

                System.out.println("O que você deseja atualizar?");
                System.out.println("1 - Nome");
                System.out.println("2 - CNPJ");
                System.out.println("3 - Telefone");
                System.out.print("Opção: ");
                String escolha = scanner.nextLine();

                if (escolha.equals("1")) {
                    System.out.print("Novo Nome (" + f.getNome() + "): ");
                    String novoNome = scanner.nextLine();
                    if (novoNome.isBlank()) throw new RuntimeException("Nome inválido!");
                    f.setNome(novoNome);
                } else if (escolha.equals("2")) {
                    System.out.print("Novo CNPJ (" + f.getCnpj() + "): ");
                    String novoCnpj = scanner.nextLine();
                    if (novoCnpj.isBlank()) throw new RuntimeException("CNPJ inválido!");
                    f.setCnpj(novoCnpj);
                } else if (escolha.equals("3")) {
                    System.out.print("Novo Telefone (" + f.getTelefone() + "): ");
                    String novoTelefone = scanner.nextLine();
                    if (novoTelefone.isBlank()) throw new RuntimeException("Telefone inválido!");
                    f.setTelefone(novoTelefone);
                } else {
                    throw new RuntimeException("Opção inválida!");
                }

                facade.atualizarFornecedor(f);
                System.out.println("Fornecedor atualizado!");
            } else if (subOpcao.equals("4")) {
                System.out.print("ID a deletar: ");
                facade.deletarFornecedor(scanner.nextLine());
                System.out.println("Fornecedor removido.");
            }
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void emitirRelatorios() {
        System.out.println("\nEMITIR RELATÓRIOS");
        System.out.println("1 - Itens Disponíveis");
        System.out.println("2 - Aluguéis Atuais (Ativos)");
        System.out.println("3 - Aluguel de um Cliente (Histórico)");
        System.out.println("4 - Financeiro (Faturamento)");
        System.out.print("Opção: ");
        String subOpcao = scanner.nextLine();

        if (subOpcao.equals("1")) {
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

        } else if (subOpcao.equals("2")) {
            System.out.println("\nRELATÓRIO DE CONTRATOS ATIVOS");
            try {
                String relatorio = facade.gerarRelatorioItensAlugados();
                System.out.println(relatorio);
            } catch (RuntimeException e) {
                System.out.println("Erro ao gerar relatório: " + e.getMessage());
            }

        } else if (subOpcao.equals("3")) {
            System.out.println("\nRELATÓRIO DE CONTRATOS POR CLIENTE");
            System.out.print("ID do Cliente: ");
            String clienteId = scanner.nextLine();
            try {
                Map<String, ContratoAluguel> contratos = facade.consultarHistoricoCliente(clienteId);
                if (contratos.isEmpty()) {
                    System.out.println("Não há histórico de contratos para esse cliente.");
                } else {
                    for (ContratoAluguel c : contratos.values()) {
                        System.out.println("ID: " + c.getId() + " | Item: " + c.getItem().getNome() + " | Valor total: " + c.getValorTotal() + " | Status: " + c.getStatus() + " | Devolução prevista: " + c.getDataPrevDevolucao());
                    }
                }
            } catch (RuntimeException e) {
                System.out.println("Erro ao listar contratos: " + e.getMessage());
            }

        } else if (subOpcao.equals("4")) {
            System.out.println("\nRELATÓRIO FINANCEIRO");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ini = null;
            LocalDate fim = null;

            while (ini == null) {
                try {
                    System.out.print("Data Inicial (dd/MM/yyyy): ");
                    ini = LocalDate.parse(scanner.nextLine(), formatter);
                } catch (DateTimeParseException e) {
                    System.out.println("Data inválida, tente novamente.");
                }
            }

            while (fim == null) {
                try {
                    System.out.print("Data Final (dd/MM/yyyy): ");
                    fim = LocalDate.parse(scanner.nextLine(), formatter);
                    if (fim.isBefore(ini)) {
                        System.out.println("Data final não pode ser anterior à data inicial.");
                        fim = null;
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Data inválida, tente novamente.");
                }
            }

            try {
                System.out.println(facade.gerarRelatorioFaturamento(ini, fim));
            } catch (RuntimeException e) {
                System.out.println("Erro ao gerar relatório: " + e.getMessage());
            }

        } else {
            System.out.println("Opção inválida!");
        }
    }
}