package com.loja.model;

import java.math.BigDecimal;

public class Multa {
    private String id;
    private ContratoAluguel contrato;
    private String motivo;
    private BigDecimal valorFixo;
    private BigDecimal valorDiario;
    private BigDecimal valorTotal;
    private int diasAtraso;
    private String status;

    public Multa(String id, ContratoAluguel contrato, String motivo, BigDecimal valorFixo, BigDecimal valorDiario, int diasAtraso, String status){
        this.id = id;
        this.contrato = contrato;
        this.motivo = motivo;
        this.valorFixo = valorFixo;
        this.valorDiario = valorDiario;
        this.diasAtraso = diasAtraso;
        this.status = status;
        this.valorTotal = valorDiario.multiply(BigDecimal.valueOf(diasAtraso)); // multiplica o bigdecimal por um int

        BigDecimal totalDiario = valorDiario.multiply(BigDecimal.valueOf(diasAtraso));
        this.valorTotal = valorFixo.add(totalDiario);
    }

    public String getId(){
        return id;
    }
    public ContratoAluguel getContrato(){
        return contrato;
    }
    public String getMotivo(){
        return motivo;
    }
    public BigDecimal getValorFixo() {
        return valorFixo;
    }
    public BigDecimal getValorDiario() {
        return valorDiario;
    }
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    public int getDiasAtraso(){
        return diasAtraso;
    }
    public String getStatus(){
        return status;
    }

    public void setId(String id){
        this.id = id;
    }
    public void setContrato(ContratoAluguel contrato){
        this.contrato = contrato;
    }
    public void setMotivo(String motivo){
        this.motivo = motivo;
    }
    public void setValorFixo(BigDecimal valorFixo){
        this.valorFixo = valorFixo;
    }
    public void setValorDiario(BigDecimal valorDiario) {
        this.valorDiario = valorDiario;
    }
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    public void setDiasAtraso(int diasAtraso){
        this.diasAtraso = diasAtraso;
    }
    public void setStatus(String status){
        this.status = status;
    }
}