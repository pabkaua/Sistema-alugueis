package com.loja.ui;

import com.loja.model.Administrador;
import com.loja.model.Cliente;
import com.loja.model.Funcionario;
import com.loja.model.Usuario;
import com.loja.padraoFacade.interfaces.ILojaFacade;

import java.util.Scanner;

public class MenuLogin {
    private final ILojaFacade facade;
    private final Scanner scanner;

    public MenuLogin(ILojaFacade facade){
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar(){
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n=== BEM-VINDO À LOJA QUE ALUGA DE UM TUDO ===");
            System.out.println("1 - Login");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> exibirMenuLogin();
                case "0" -> {
                    scanner.close();
                    facade.salvarTudo();
                    System.out.println("Encerrando o sistema...");
                    rodando = false;
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void exibirMenuLogin() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            Usuario usuario = facade.autenticarUsuario(email, senha);
            if (usuario == null) {
                System.out.println("E-mail ou senha incorretos.");
            } else {
                redirecionar(usuario);
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao autenticar: " + e.getMessage());
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