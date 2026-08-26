package com.loja.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContratoAluguel {
    private String id;
    private Cliente cliente;
    private Item item;
    private LocalDate dataRetirada;
    private LocalDate dataPrevDevolucao;
    private LocalDate dataEfetivaDevolucao;
    private BigDecimal valorTotal;
    private String status;
    private Boolean historico;

    public ContratoAluguel(String id, Cliente cliente, Item item, LocalDate dataRetirada, LocalDate dataPrevDevolucao, LocalDate dataEfetivaDevolucao, BigDecimal valorTotal, String status){
        this.id = id;
        this.cliente = cliente;
        this.item = item;
        this.dataRetirada = dataRetirada;
        this.dataPrevDevolucao = dataPrevDevolucao;
        this.dataEfetivaDevolucao = dataEfetivaDevolucao;
        this.valorTotal = valorTotal;
        this.status = status;
        this.historico = false;
    }

    public ContratoAluguel(){
        //construtor vazio (casca para MultaPersistencia)
    }

    //getters:
    public String getId(){
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Item getItem() {
        return item;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public LocalDate getDataPrevDevolucao() {
        return dataPrevDevolucao;
    }

    public LocalDate getDataEfetivaDevolucao() {
        return dataEfetivaDevolucao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getHistorico() {
        return historico;
    }

    //setters:
    public void setId(String id) {
        this.id = id;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setDataRetirada(LocalDate dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public void setDataPrevDevolucao(LocalDate dataPrevDevolucao) {
        this.dataPrevDevolucao = dataPrevDevolucao;
    }

    public void setDataEfetivaDevolucao(LocalDate dataEfetivaDevolucao) {
        this.dataEfetivaDevolucao = dataEfetivaDevolucao;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHistorico(Boolean historico) {
        this.historico = historico;
    }
}
