package com.loja.model;
import jakarta.persistence.Entity;

@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {

    private boolean inadimplente;
    private boolean historico;


    public Cliente(String nome, String login, String senha) {
        super(nome, login, senha);
        this.inadimplente = false;
        this.historico = false;
    }
    public Cliente(){
        super();
    }


    public Cliente(String id, String nome, String login, String senha) {
        super(nome, login, senha);
        this.inadimplente = false;
        this.historico = false;
    }

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
