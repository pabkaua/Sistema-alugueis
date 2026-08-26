package com.loja.model;

public class Categoria {
    private String id;
    private String nome;
    private boolean historico;

    public Categoria(String id, String nome){
        this.id = id;
        this.nome = nome;
        this.historico = false;
    }

    public Categoria(){};

    public String getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public boolean hasHistorico(){
        return this.historico;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setHistorico(boolean historico) {
        this.historico = historico;
    }
}
