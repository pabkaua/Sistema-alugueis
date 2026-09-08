package com.loja.model;

public class Administrador extends Usuario {

    private int nivelAcesso;
    private String departamento;

    public Administrador(String id, String nome, String login, String senha, int nivelAcesso, String departamento) {
        super(id, nome, login, senha);
        this.nivelAcesso = nivelAcesso;
        this.departamento = departamento;
    }

    public Administrador(String id, String nome, String login, String senha) {
        super(id, nome, login, senha);
        this.nivelAcesso = 1;
        this.departamento = "Geral";
    }

    @Override
    public String getPerfil(){
        return "ADMINISTRADOR";
    }

    public int getNivelAcesso(){
        return nivelAcesso;
    }
    public String getDepartamento(){
        return departamento;
    }

    public void setNivelAcesso(int nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}