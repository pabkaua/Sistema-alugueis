package com.loja.model;

public abstract class Usuario {

    private String id;
    private String nome;
    private String login;
    private String senha;
    private boolean ativo;

    public Usuario(String id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
    }
    public Usuario(){};

    public abstract String getPerfil();

    public String getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getLogin() {
        return login;
    }
    public String getSenha() {
        return senha;
    }
    public boolean isAtivo() {
        return ativo;
    }

    public void setId(String id){
        this.id = id;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}