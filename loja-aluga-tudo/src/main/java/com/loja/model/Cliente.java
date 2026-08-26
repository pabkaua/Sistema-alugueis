package com.loja.model;

public class Cliente extends Usuario {

    private boolean inadimplente;
    private boolean historico;

    public Cliente(String id, String nome, String login, String senha) {
        super(id,nome, login, senha);
        this.inadimplente = false;
        this.historico = false;
    }
    public Cliente(){
        super();
    };

    @Override
    public String getPerfil(){
        return "CLIENTE";
    }

    public boolean isInadimplente(){
        return inadimplente;
    }
    public boolean hasHistorico(){
        return this.historico;
    }

    public void setInadimplente(boolean inadimplente) {
        this.inadimplente = inadimplente;
    }
    public void setHistorico(boolean historico) {
        this.historico = historico;
    }
}