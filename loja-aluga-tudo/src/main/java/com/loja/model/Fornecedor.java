package com.loja.model;

public class Fornecedor {
    private String id;
    private String nome;
    private String cnpj;
    private String telefone;
    private boolean historico;

    public Fornecedor(String id, String nome, String cnpj, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.historico = false;
    }
    public Fornecedor(){};

    public String getId(){
        return this.id;
    }
    public String getNome(){
        return this.nome;
    }
    public String getCnpj(){
        return this.cnpj;
    }
    public String getTelefone(){
        return this.telefone;
    }
    public boolean hasHistorico() {
        return this.historico;
    }

    public void setId(String id){
        this.id = id;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public void setCnpj(String cnpj){
        this.cnpj = cnpj;
    }
    public void setTelefone(String telefone){
        this.telefone = telefone;
    }
    public void setHistorico(boolean historico) {
        this.historico = historico;
    }
}
