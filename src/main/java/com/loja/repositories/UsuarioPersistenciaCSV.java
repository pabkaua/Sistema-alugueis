package com.loja.repositories;

import com.loja.model.Administrador;
import com.loja.model.Cliente;
import com.loja.model.Funcionario;
import com.loja.model.Usuario;
import com.loja.repositories.interfaces.IUsuarioRepository;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioPersistenciaCSV implements IUsuarioRepository {

    private String caminhoArquivo;
    private Map<String, Usuario> usuarios;

    public UsuarioPersistenciaCSV(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        this.usuarios = new HashMap<>();
        this.carregarDados();
    }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }

    @Override
    public void salvar(Usuario usuario) {
        usuarios.put(usuario.getId().toUpperCase(), usuario);
    }

    @Override
    public Usuario buscar(String id) {
        return usuarios.get(id.toUpperCase());
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return this.usuarios.values().stream()
                .filter(usuario -> usuario.getLogin().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Map<String, Usuario> listar() {
        return Collections.unmodifiableMap(this.usuarios);
    }

    @Override
    public Map<String, Usuario> listar(String perfil) {
        return this.usuarios.entrySet().stream()
                .filter(entry -> entry.getValue().getPerfil().equalsIgnoreCase(perfil))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        if (usuarios.containsKey(usuario.getId())) {
            usuarios.put(usuario.getId(), usuario);
            return true;
        }
        return false;
    }

    @Override
    public boolean deletar(String id) {
        if (usuarios.containsKey(id)) {
            usuarios.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void carregarDados() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.caminhoArquivo))) {
            String cabecalho = br.readLine();
            if (cabecalho == null) {
                return; // Arquivo vazio
            }
            String linha;
            while ((linha = br.readLine()) != null) {
                processarLinha(linha);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro I/O ao carregar dados do arquivo CSV de usuário ->", e);
        }
    }

    private void processarLinha(String linha) {
        if (linha.trim().isEmpty()) return;

        String[] dados = linha.split(";", -1);
        if (dados.length < 6) return;

        Usuario usuario = criarUsuario(dados);
        if (usuario == null) return;

        usuario.setAtivo(Boolean.parseBoolean(dados[5]));
        this.usuarios.put(dados[0].toUpperCase(), usuario);
    }

    private Usuario criarUsuario(String[] dados) {
        String id = dados[0].toUpperCase();
        String nome = dados[1];
        String login = dados[2];
        String senha = dados[3];
        String perfil = dados[4];

        if (perfil.equalsIgnoreCase("ADMINISTRADOR")) {
            return criarAdministrador(dados, id, nome, login, senha);
        }
        if (perfil.equalsIgnoreCase("FUNCIONARIO")) {
            return criarFuncionario(dados, id, nome, login, senha);
        }
        if (perfil.equalsIgnoreCase("CLIENTE")) {
            return criarCliente(dados, id, nome, login, senha);
        }
        return null;
    }

    private Administrador criarAdministrador(String[] dados, String id, String nome, String login, String senha) {
        int nivelAcesso = campoInt(dados, 6, 1);
        String departamento = campoTexto(dados, 7, "Geral");
        return new Administrador(id, nome, login, senha, nivelAcesso, departamento);
    }

    private Funcionario criarFuncionario(String[] dados, String id, String nome, String login, String senha) {
        String cargo = campoTexto(dados, 6, "Geral");
        return new Funcionario(id, nome, login, senha, cargo);
    }

    private Cliente criarCliente(String[] dados, String id, String nome, String login, String senha) {
        Cliente cliente = new Cliente(id, nome, login, senha);
        if (dados.length > 6 && !dados[6].isEmpty()) {
            cliente.setInadimplente(Boolean.parseBoolean(dados[6]));
        }
        return cliente;
    }

    private String campoTexto(String[] dados, int indice, String valorPadrao) {
        return dados.length > indice && !dados[indice].isEmpty() ? dados[indice] : valorPadrao;
    }

    private int campoInt(String[] dados, int indice, int valorPadrao) {
        return dados.length > indice && !dados[indice].isEmpty() ? Integer.parseInt(dados[indice]) : valorPadrao;
    }

    @Override
    public void salvarDados() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            escritor.write("id;nome;login;senha;perfil;ativo;campoExtra1;campoExtra2");
            escritor.newLine();

            for (Usuario usuario : usuarios.values()) {
                String campoExtra1 = "";
                String campoExtra2 = "";

                if (usuario instanceof Administrador administrador) {
                    campoExtra1 = String.valueOf(administrador.getNivelAcesso());
                    campoExtra2 = administrador.getDepartamento();
                } else if (usuario instanceof Funcionario funcionario) {
                    campoExtra1 = funcionario.getCargo();
                } else if (usuario instanceof Cliente cliente) {
                    campoExtra1 = String.valueOf(cliente.isInadimplente());
                }

                String linha =
                        usuario.getId().toUpperCase() + ";" +
                        usuario.getNome() + ";" +
                        usuario.getLogin() + ";" +
                        usuario.getSenha() + ";" +
                        usuario.getPerfil() + ";" +
                        usuario.isAtivo() + ";" +
                        campoExtra1 + ";" +
                        campoExtra2;
                escritor.write(linha);
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro I/O ao salvar dados de usuário no arquivo CSV  ->", e);
        }
    }
}
