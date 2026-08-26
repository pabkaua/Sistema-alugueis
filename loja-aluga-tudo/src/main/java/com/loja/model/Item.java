package com.loja.model;
import java.math.BigDecimal;

public class Item {
    private String id;
    private String nome;
    private BigDecimal taxaDiaria;
    private BigDecimal valorReposicao;
    private String status;
    private Categoria categoria;
    private Fornecedor fornecedor;
    private boolean historico;

    public Item(String id, String nome, BigDecimal taxaDiaria, BigDecimal valorReposicao, String status, Categoria categoria, Fornecedor fornecedor){
        this.id = id;
        this.nome = nome;
        this.taxaDiaria = taxaDiaria;
        this.valorReposicao = valorReposicao;
        this.status = status;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
        this.historico = false;
    }
    public Item(){};

    public String getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public BigDecimal getTaxaDiaria() {
        return taxaDiaria;
    }
    public BigDecimal getValorReposicao() {
        return valorReposicao;
    }
    public String getStatus() {
        return status;
    }
    public Categoria getCategoria() {
        return categoria;
    }
    public Fornecedor getFornecedor() {
        return fornecedor;
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
    public void setTaxaDiaria(BigDecimal taxaDiaria) {
        this.taxaDiaria = taxaDiaria;
    }
    public void setValorReposicao(BigDecimal valorReposicao) {
        this.valorReposicao = valorReposicao;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }
    public void setHistorico(boolean historico) {
        this.historico = historico;
    }
}

