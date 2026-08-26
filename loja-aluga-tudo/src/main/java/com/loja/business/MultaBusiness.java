package com.loja.business;

import com.loja.model.ContratoAluguel;
import com.loja.model.Multa;
import com.loja.repositories.interfaces.IMultaRepository;
import com.loja.business.interfaces.IMultaBusiness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class MultaBusiness implements IMultaBusiness{

    private IMultaRepository multaRepository;
    private static final BigDecimal valorFixoPenalidade = new BigDecimal("20.00");
    private static final BigDecimal valorTaxaDiaria = new BigDecimal ("5.50");

    public MultaBusiness(IMultaRepository multaRepository){
        this.multaRepository = multaRepository;
    }

    public void aplicar(ContratoAluguel contrato){
        if (contrato == null){
            throw new RuntimeException("Não é possível aplicar multa para um contrato nulo.");
        }

        BigDecimal valorAtraso = calcularAtraso(contrato);

        if (valorAtraso.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Este contrato não possui dias de atraso para aplicação da multa.");
        }

        long dias = ChronoUnit.DAYS.between(contrato.getDataPrevDevolucao(), contrato.getDataEfetivaDevolucao());
        int diasAtraso = (int) dias;

        Multa novaMulta = new Multa(null, contrato, "Atraso na devolução do item", valorFixoPenalidade, valorTaxaDiaria, diasAtraso, "PENDENTE");

        multaRepository.salvar(novaMulta);
    }

    @Override
    public void quitar(String multaId){
        if (multaId == null || multaId.trim().isEmpty()){
            throw new RuntimeException("ID da multa inválido para operação de quitação.");
        }
        Multa existente = multaRepository.buscar(multaId);

        if (existente == null){
            throw new RuntimeException("Multa não encontrada para o ID: " + multaId);
        }

        existente.setStatus("QUITADA");
        multaRepository.salvar(existente);
    }

    @Override
    public Map<String, Multa> listarPorCliente(String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) {
            throw new RuntimeException("ID do cliente inválido para a consulta.");
        }
        
        Map<String, Multa> multasFiltradas = new HashMap<>();

        for (Multa multa : multaRepository.listar().values()) {
            if (multa.getContrato() != null 
                    && multa.getContrato().getCliente() != null 
                    && clienteId.equals(multa.getContrato().getCliente().getId())) {
    
                multasFiltradas.put(multa.getId(), multa);
            }
        }

        return multasFiltradas;
    }

    @Override
    public BigDecimal calcularAtraso(ContratoAluguel contrato){
        if (contrato == null){
            return BigDecimal.ZERO;
        }
        LocalDate dataFinalCalculo = contrato.getDataEfetivaDevolucao() != null ? 
                                     contrato.getDataEfetivaDevolucao() : LocalDate.now();

        long diasAtraso = ChronoUnit.DAYS.between(contrato.getDataPrevDevolucao(), dataFinalCalculo);

        if (diasAtraso <= 0){
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiario = valorTaxaDiaria.multiply(BigDecimal.valueOf(diasAtraso));

        return valorFixoPenalidade.add(totalDiario);
    }

    @Override
    public boolean possuiMultaPendente(String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) {
            return false;
        }

        for (Multa multa : multaRepository.listar().values()) {
            if (multa.getContrato() != null 
                    && multa.getContrato().getCliente() != null 
                    && clienteId.equals(multa.getContrato().getCliente().getId())
                    && "PENDENTE".equalsIgnoreCase(multa.getStatus())) {
                return true; 
            }
        }

        return false; 
    }

    @Override
    public Map<String, Multa> listar() {
        return multaRepository.listar();
    }

    @Override
    public Multa buscar(String Id){
        if (Id == null || Id.trim().isEmpty()) {
            throw new RuntimeException("ID fornecido é inválido para busca.");
        }
        Multa multa = multaRepository.buscar(Id);
        if(multa == null){
            throw new RuntimeException("Multa não encontrada para o Id: " + Id);
        }
        return multa;
    }

    @Override
    public void atualizar(Multa multa){
        if (multa == null){
            throw new RuntimeException("Multa inválida para atualização!");
        }
        this.buscar(multa.getId());
        multaRepository.atualizar(multa);
    }

    @Override
    public void deletarMulta(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("ID inválido para exclusão da multa.");
        }
        
        boolean deletado = multaRepository.deletar(id);
        
        if (!deletado) {
            throw new RuntimeException("Não foi possível deletar: Multa não encontrada com o ID: " + id);
        }
    }

    public void salvarDados(){
        this.multaRepository.salvarDados();
    }
}