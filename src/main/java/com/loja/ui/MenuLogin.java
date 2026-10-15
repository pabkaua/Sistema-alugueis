package com.loja.ui;

import com.loja.model.Administrador;
import com.loja.model.Cliente;
import com.loja.model.Funcionario;
import com.loja.model.Usuario;
import com.loja.padraofacade.interfaces.ILojaFacade;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuLogin {

    private static final Logger logger =
            Logger.getLogger(MenuLogin.class.getName());

    private final ILojaFacade facade;
    private final Scanner scanner;

    public MenuLogin(ILojaFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        boolean rodando = true;

        while (rodando) {
            logger.info("\n=== BEM-VINDO À LOJA QUE ALUGA DE UM TUDO ===");
            logger.info("1 - Login");
            logger.info("0 - Sair");
            logger.info("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> exibirMenuLogin();

                case "0" -> {
                    scanner.close();
                    facade.salvarTudo();
                    logger.info("Encerrando o sistema...");
                    rodando = false;
                }

                default -> logger.info("Opção inválida!");
            }
        }
    }

    private void exibirMenuLogin() {
        logger.info("\n=== LOGIN ===");
        logger.info("E-mail: ");

        String email = scanner.nextLine();

        logger.info("Senha: ");

        String senha = scanner.nextLine();

        try {
            Usuario usuario = facade.autenticarUsuario(email, senha);

            if (usuario == null) {
                logger.info("E-mail ou senha incorretos.");
            } else {
                redirecionar(usuario);
            }

        } catch (RuntimeException e) {
            logger.log(
                    Level.SEVERE,
                    "Erro ao autenticar: {0}",
                    e.getMessage()
            );
        }
    }

    private void redirecionar(Usuario usuario) {
        if (usuario instanceof Administrador adm) {
            new MenuAdmin(facade, adm, scanner).exibir();

        } else if (usuario instanceof Funcionario func) {
            new MenuFuncionario(facade, func, scanner).exibir();

        } else if (usuario instanceof Cliente cliente) {
            new MenuCliente(facade, cliente, scanner).exibir();
        }
    }
}