package com.loja.model;

public class Funcionario extends Usuario {

    private String cargo;
    private boolean historico;

    public Funcionario(String id, String nome, String login, String senha, String cargo) {
        super(id, nome, login, senha);
        this.cargo = cargo;
        this.historico = false;
    }

    public String getPerfil(){
        return "FUNCIONARIO";
    }
    public String getCargo() {
        return cargo;
    }
    public boolean hasHistorico(){
        return this.historico;
    }

    public void setCargo(String cargo){
        this.cargo = cargo;
    }
    public void setHistorico(boolean historico) {
        this.historico = historico;
    }
}