package com.loja.model;
import jakarta.persistence.*;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcionario")
public class Funcionario extends Usuario {

    private String cargo;
    private boolean historico;

    protected Funcionario(){}

    public Funcionario(String id, String nome, String login, String senha, String cargo) {
        super(nome, login, senha);
        this.cargo = cargo;
        this.historico = false;
        //Legado
        setId(id);
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
