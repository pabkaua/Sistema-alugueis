package com.loja.ui;

import com.loja.model.*;
import com.loja.padraofacade.interfaces.ILojaFacade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MenuAdmin {

    private static final Logger LOGGER = Logger.getLogger(MenuAdmin.class.getName());

    private static final String ESCOLHA_UMA_OPCAO = "Escolha uma opção: ";
    private static final String OPCAO = "Opção: ";
    private static final String OPCAO_INVALIDA = "Opção inválida!";
    private static final String ROTULO_ID = "ID: ";
    private static final String ROTULO_NOME = "Nome: ";
    private static final String SEPARADOR_NOME = " | Nome: ";
    private static final String PREFIXO_ERRO = "Erro: ";
    private static final String ID_A_DELETAR = "ID a deletar: ";
    private static final String O_QUE_ATUALIZAR = "O que você deseja atualizar?";
    private static final String OPCAO_NOME = "1 - Nome";
    private static final String VALOR_NEGATIVO = "O valor não pode ser negativo!";
    private static final String ERRO_GERAR_RELATORIO = "Erro ao gerar relatório: ";

    static {
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord registro) {
                return registro.getMessage() + System.lineSeparator();
            }
        });
        LOGGER.addHandler(handler);
    }

    private final ILojaFacade facade;
    private final Administrador usuarioLogado;
    private final Scanner scanner;

    public MenuAdmin(ILojaFacade facade, Administrador usuarioLogado, Scanner scanner) {
        this.facade = facade;
        this.usuarioLogado = usuarioLogado;
        this.scanner = scanner;
    }

    private void mostrar(String texto) {
        LOGGER.info(texto);
    }

    private String ler(String pergunta) {
        mostrar(pergunta);
        return scanner.nextLine();
    }

    private BigDecimal lerValorNaoNegativo(String pergunta, String mensagemErro) {
        BigDecimal valor;
        try {
            valor = new BigDecimal(ler(pergunta));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensagemErro);
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(VALOR_NEGATIVO);
        }
        return valor;
    }

    public void exibir() {
        boolean ativo = true;
        while (ativo) {
            mostrar("\n=== PAINEL ADMINISTRATIVO: " + usuarioLogado.getNome().toUpperCase() + " ===");
            mostrar("1 - Gerenciar Usuários");
            mostrar("2 - Gerenciar Itens");
            mostrar("3 - Gerenciar Categorias");
            mostrar("4 - Gerenciar Fornecedores");
            mostrar("5 - Emitir Relatórios");
            mostrar("0 - Sair");

            String opcao = ler(ESCOLHA_UMA_OPCAO);

            switch (opcao) {
                case "1" -> gerenciarUsuarios();
                case "2" -> gerenciarItens();
                case "3" -> gerenciarCategorias();
                case "4" -> gerenciarFornecedores();
                case "5" -> emitirRelatorios();
                case "0" -> {
                    mostrar("Saindo do painel administrativo...");
                    ativo = false;
                }
                default -> mostrar(OPCAO_INVALIDA);
            }
        }
    }

    private void gerenciarUsuarios() {
        mostrar("\nGERENCIAR USUÁRIOS");
        mostrar("1 - Cadastrar Usuário (Cliente/Func/Adm)");
        mostrar("2 - Listar Usuários");
        mostrar("3 - Atualizar Usuário");
        mostrar("4 - Desativar Usuário");
        String subOpcao = ler(ESCOLHA_UMA_OPCAO);

        try {
            if (subOpcao.equals("1")) {
                mostrar("Tipo: 1-Cliente | 2-Funcionário | 3-Administrador");
                String tipo = ler("Escolha o tipo: ");
                String id = ler(ROTULO_ID);
                String nome = ler(ROTULO_NOME);
                String email = ler("Email/Login: ");
                String senha = ler("Senha: ");

                if (tipo.equals("1")) {
                    facade.cadastrarCliente(new Cliente(id, nome, email, senha));
                    mostrar("Cliente cadastrado com sucesso!");
                } else if (tipo.equals("2")) {
                    String cargo = ler("Cargo do Funcionário: ");
                    facade.cadastrarFuncionario(new Funcionario(id, nome, email, senha, cargo));
                    mostrar("Funcionário cadastrado com sucesso!");
                } else if (tipo.equals("3")) {
                    facade.cadastrarAdm(new Administrador(id, nome, email, senha));
                    mostrar("Administrador cadastrado com sucesso!");
                } else {
                    mostrar("Tipo de usuário inválido!");
                }

            } else if (subOpcao.equals("2")) {
                mostrar("1-Todos | 2-Por Perfil (CLIENTE/FUNCIONARIO/ADMINISTRADOR)");
                String listOpt = ler(OPCAO);

                if (listOpt.equals("1")) {
                    facade.listarUsuario().values().forEach(u -> mostrar(ROTULO_ID + u.getId() + SEPARADOR_NOME + u.getNome() + " | Perfil: " + u.getPerfil()));
                } else if (listOpt.equals("2")) {
                    String perfil = ler("Perfil desejado: ").toUpperCase();
                    Map<String, Usuario> usuarios = facade.listarUsuarioPorPerfil(perfil);
                    if (usuarios.isEmpty()) throw new IllegalStateException("Nenhum usuário de perfil " + perfil);
                    usuarios.values().forEach(u -> mostrar(ROTULO_ID + u.getId() + SEPARADOR_NOME + u.getNome()));
                } else {
                    mostrar("Digite uma opção válida!");
                }

            } else if (subOpcao.equals("3")) {
                String id = ler("ID do usuário a atualizar: ");

                Usuario u = facade.buscarUsuario(id);

                mostrar(O_QUE_ATUALIZAR);
                mostrar(OPCAO_NOME);
                mostrar("2 - Email/Login");
                mostrar("3 - Senha");
                mostrar("4 - Cargo (quando aplicavel)");
                String escolha = scanner.nextLine();

                if (escolha.equals("1")) {
                    String novoNome = ler("Novo Nome (" + u.getNome() + "): ");
                    if (novoNome.isBlank()) throw new IllegalArgumentException("nome inválido!");
                    u.setNome(novoNome);
                } else if (escolha.equals("2")) {
                    String novoLogin = ler("Novo Email/Login (" + u.getLogin() + "): ");
                    if (novoLogin.isBlank()) throw new IllegalArgumentException("login inválido!");
                    u.setLogin(novoLogin);
                } else if (escolha.equals("3")) {
                    String novaSenha = ler("Nova senha: ");
                    if (novaSenha.isBlank() || novaSenha.length() < 3) throw new IllegalArgumentException("Senha inválida!");
                    u.setSenha(novaSenha);
                } else if (escolha.equals("4") && !(u instanceof Funcionario)) {
                    throw new IllegalStateException("O usuário não é funcionário!");
                } else if (escolha.equals("4")) {
                    Funcionario funcionario = (Funcionario) u;
                    String novoCargo = ler("Novo cargo (" + funcionario.getCargo() + "): ");
                    if (novoCargo.isBlank()) throw new IllegalArgumentException("Cargo inválido!");
                    funcionario.setCargo(novoCargo);
                } else {
                    throw new IllegalArgumentException(OPCAO_INVALIDA);
                }

                facade.atualizarUsuario(id, u);
                mostrar("Usuário atualizado com sucesso!");

            } else if (subOpcao.equals("4")) {
                String id = ler("ID do usuário a desativar: ");
                facade.desativarUsuario(id);
                mostrar("Usuário desativado com sucesso.");
            }

        } catch (RuntimeException e) {
            mostrar(PREFIXO_ERRO + e.getMessage());
        }
    }

    private void gerenciarItens() {
        mostrar("\nGERENCIAR ITENS");
        mostrar("1 - Cadastrar Item");
        mostrar("2 - Listar Itens");
        mostrar("3 - Atualizar Item");
        mostrar("4 - Deletar Item");
        String subOpcao = ler(ESCOLHA_UMA_OPCAO);

        try {
            if (subOpcao.equals("1")) {
                Item item = new Item();

                item.setId(ler(ROTULO_ID));
                item.setNome(ler(ROTULO_NOME));
                item.setStatus("DISPONIVEL");
                item.setCategoria(facade.buscarCategoria(ler("ID Categoria: ")));
                item.setFornecedor(facade.buscarFornecedor(ler("ID Fornecedor: ")));
                item.setTaxaDiaria(lerValorNaoNegativo("Taxa Diária (XX.xx):  R$ ", "Valor inválido para taxa diária."));
                item.setValorReposicao(lerValorNaoNegativo("Valor de reposição (XX.xx): R$ ", "Valor inválido para o valor de reposição."));

                facade.cadastrarItem(item);
                mostrar("Item cadastrado!");

            } else if (subOpcao.equals("2")) {
                mostrar("1-Todos | 2-Por Status | 3-Por Categoria | 4-Por Fornecedor");
                String opt = ler(OPCAO);

                if (opt.equals("1")) {
                    facade.listarItem().values().forEach(i -> mostrar(ROTULO_ID + i.getId() + SEPARADOR_NOME + i.getNome() + " | Status: " + i.getStatus()));
                } else if (opt.equals("2")) {
                    String status = ler("Status (DISPONIVEL/ALUGADO): ").toUpperCase();
                    facade.listarItemPorStatus(status).values().forEach(i -> mostrar(ROTULO_ID + i.getId() + SEPARADOR_NOME + i.getNome()));
                } else if (opt.equals("3")) {
                    Categoria cat = facade.buscarCategoria(ler("ID Categoria: "));
                    facade.listarItemPorCategoria(cat).values().forEach(i -> mostrar(ROTULO_ID + i.getId() + SEPARADOR_NOME + i.getNome()));
                } else if (opt.equals("4")) {
                    Fornecedor forn = facade.buscarFornecedor(ler("ID Fornecedor: "));
                    facade.listarItemPorFornecedor(forn).values().forEach(i -> mostrar(ROTULO_ID + i.getId() + SEPARADOR_NOME + i.getNome()));
                }

            } else if (subOpcao.equals("3")) {
                Item item = facade.buscarItem(ler("ID do Item: "));

                mostrar(O_QUE_ATUALIZAR);
                mostrar(OPCAO_NOME);
                mostrar("2 - Taxa diária");
                mostrar("3 - Valor de reposição");
                mostrar("4 - Categoria");
                mostrar("5 - Fornecedor");
                String escolha = scanner.nextLine();

                if (escolha.equals("1")) {
                    String novoNome = ler("Novo Nome (" + item.getNome() + "): ");
                    if (novoNome.isBlank()) throw new IllegalArgumentException("nome inválido!");
                    item.setNome(novoNome);
                } else if (escolha.equals("2")) {
                    item.setTaxaDiaria(lerValorNaoNegativo(
                            "Nova taxa diária (" + item.getTaxaDiaria() + ")(XX.xx):  R$ ",
                            "Valor inválido para taxa diária."));
                } else if (escolha.equals("3")) {
                    item.setValorReposicao(lerValorNaoNegativo(
                            "Valor de reposição (XX.xx): R$ ",
                            "Valor inválido para o valor de reposição."));
                } else if (escolha.equals("4")) {
                    String novaCategoriaId = ler("Digite o id da categoria (" + item.getCategoria().getId() + "): ");
                    item.setCategoria(facade.buscarCategoria(novaCategoriaId));
                } else if (escolha.equals("5")) {
                    String novoFornecedorId = ler("Digite o id do fornecedor (" + item.getFornecedor().getId() + "): ");
                    item.setFornecedor(facade.buscarFornecedor(novoFornecedorId));
                } else {
                    throw new IllegalArgumentException(OPCAO_INVALIDA);
                }

                facade.atualizarItem(item);
                mostrar("Item atualizado com sucesso!");

            } else if (subOpcao.equals("4")) {
                facade.deletarItem(ler("ID do Item a deletar: "));
                mostrar("Item deletado do repositório.");
            }
        } catch (RuntimeException e) {
            mostrar(PREFIXO_ERRO + e.getMessage());
        }
    }

    private void gerenciarCategorias() {
        mostrar("\nGERENCIAR CATEGORIAS");
        mostrar("1 - Cadastrar");
        mostrar("2 - Listar");
        mostrar("3 - Atualizar");
        mostrar("4 - Deletar");
        String subOpcao = ler(OPCAO);

        try {
            if (subOpcao.equals("1")) {
                String id = ler(ROTULO_ID);
                String nome = ler(ROTULO_NOME);
                facade.cadastrarCategoria(new Categoria(id, nome));
                mostrar("Categoria criada!");

            } else if (subOpcao.equals("2")) {
                facade.listarCategoria().values().forEach(c -> mostrar(ROTULO_ID + c.getId() + SEPARADOR_NOME + c.getNome()));

            } else if (subOpcao.equals("3")) {
                Categoria c = facade.buscarCategoria(ler(ROTULO_ID));
                c.setNome(ler("Novo Nome: "));
                facade.atualizarCategoria(c);
                mostrar("Categoria atualizada!");

            } else if (subOpcao.equals("4")) {
                facade.deletarCategoria(ler(ID_A_DELETAR));
                mostrar("Categoria removida.");
            }
        } catch (RuntimeException e) {
            mostrar(PREFIXO_ERRO + e.getMessage());
        }
    }

    private void gerenciarFornecedores() {
        mostrar("\nGERENCIAR FORNECEDORES");
        mostrar("1 - Cadastrar");
        mostrar("2 - Listar");
        mostrar("3 - Atualizar");
        mostrar("4 - Deletar");
        String subOpcao = ler(OPCAO);

        try {
            if (subOpcao.equals("1")) {
                String id = ler(ROTULO_ID);
                String nome = ler(ROTULO_NOME);
                String cnpj = ler("CNPJ: ");
                String telefone = ler("Telefone: ");

                facade.cadastrarFornecedor(new Fornecedor(id, nome, cnpj, telefone));
                mostrar("Fornecedor criado!");

            } else if (subOpcao.equals("2")) {
                facade.listarFornecedor().values().forEach(f -> mostrar(ROTULO_ID + f.getId() + SEPARADOR_NOME + f.getNome() + " | CNPJ: " + f.getCnpj() + " | Telefone: " + f.getTelefone()));

            } else if (subOpcao.equals("3")) {
                Fornecedor f = facade.buscarFornecedor(ler(ROTULO_ID));

                mostrar(O_QUE_ATUALIZAR);
                mostrar(OPCAO_NOME);
                mostrar("2 - CNPJ");
                mostrar("3 - Telefone");
                String escolha = ler(OPCAO);

                if (escolha.equals("1")) {
                    String novoNome = ler("Novo Nome (" + f.getNome() + "): ");
                    if (novoNome.isBlank()) throw new IllegalArgumentException("Nome inválido!");
                    f.setNome(novoNome);
                } else if (escolha.equals("2")) {
                    String novoCnpj = ler("Novo CNPJ (" + f.getCnpj() + "): ");
                    if (novoCnpj.isBlank()) throw new IllegalArgumentException("CNPJ inválido!");
                    f.setCnpj(novoCnpj);
                } else if (escolha.equals("3")) {
                    String novoTelefone = ler("Novo Telefone (" + f.getTelefone() + "): ");
                    if (novoTelefone.isBlank()) throw new IllegalArgumentException("Telefone inválido!");
                    f.setTelefone(novoTelefone);
                } else {
                    throw new IllegalArgumentException(OPCAO_INVALIDA);
                }

                facade.atualizarFornecedor(f);
                mostrar("Fornecedor atualizado!");
            } else if (subOpcao.equals("4")) {
                facade.deletarFornecedor(ler(ID_A_DELETAR));
                mostrar("Fornecedor removido.");
            }
        } catch (RuntimeException e) {
            mostrar(PREFIXO_ERRO + e.getMessage());
        }
    }

    private void emitirRelatorios() {
        mostrar("\nEMITIR RELATÓRIOS");
        mostrar("1 - Itens Disponíveis");
        mostrar("2 - Aluguéis Atuais (Ativos)");
        mostrar("3 - Aluguel de um Cliente (Histórico)");
        mostrar("4 - Financeiro (Faturamento)");
        String subOpcao = ler(OPCAO);

        if (subOpcao.equals("1")) {
            mostrar("\nITENS DISPONÍVEIS");
            try {
                Map<String, Item> itens = facade.listarItensDisponiveis();
                if (itens.isEmpty()) {
                    mostrar("Não há itens disponíveis para aluguel no momento.");
                } else {
                    for (Item item : itens.values()) {
                        mostrar(ROTULO_ID + item.getId() + SEPARADOR_NOME + item.getNome() + " | Valor Diário: " + item.getTaxaDiaria());
                    }
                }
            } catch (RuntimeException e) {
                mostrar("Erro ao listar itens: " + e.getMessage());
            }

        } else if (subOpcao.equals("2")) {
            mostrar("\nRELATÓRIO DE CONTRATOS ATIVOS");
            try {
                String relatorio = facade.gerarRelatorioItensAlugados();
                mostrar(relatorio);
            } catch (RuntimeException e) {
                mostrar(ERRO_GERAR_RELATORIO + e.getMessage());
            }

        } else if (subOpcao.equals("3")) {
            mostrar("\nRELATÓRIO DE CONTRATOS POR CLIENTE");
            String clienteId = ler("ID do Cliente: ");
            try {
                Map<String, ContratoAluguel> contratos = facade.consultarHistoricoCliente(clienteId);
                if (contratos.isEmpty()) {
                    mostrar("Não há histórico de contratos para esse cliente.");
                } else {
                    for (ContratoAluguel c : contratos.values()) {
                        mostrar(ROTULO_ID + c.getId() + " | Item: " + c.getItem().getNome() + " | Valor total: " + c.getValorTotal() + " | Status: " + c.getStatus() + " | Devolução prevista: " + c.getDataPrevDevolucao());
                    }
                }
            } catch (RuntimeException e) {
                mostrar("Erro ao listar contratos: " + e.getMessage());
            }

        } else if (subOpcao.equals("4")) {
            mostrar("\nRELATÓRIO FINANCEIRO");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ini = null;
            LocalDate fim = null;

            while (ini == null) {
                try {
                    ini = LocalDate.parse(ler("Data Inicial (dd/MM/yyyy): "), formatter);
                } catch (DateTimeParseException e) {
                    mostrar("Data inválida, tente novamente.");
                }
            }

            while (fim == null) {
                try {
                    fim = LocalDate.parse(ler("Data Final (dd/MM/yyyy): "), formatter);
                    if (fim.isBefore(ini)) {
                        mostrar("Data final não pode ser anterior à data inicial.");
                        fim = null;
                    }
                } catch (DateTimeParseException e) {
                    mostrar("Data inválida, tente novamente.");
                }
            }

            try {
                mostrar(facade.gerarRelatorioFaturamento(ini, fim));
            } catch (RuntimeException e) {
                mostrar(ERRO_GERAR_RELATORIO + e.getMessage());
            }

        } else {
            mostrar(OPCAO_INVALIDA);
        }
    }
}