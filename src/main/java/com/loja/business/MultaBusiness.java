package com.loja.business;

import com.loja.business.interfaces.IMultaBusiness;
import com.loja.exceptions.MultaNaoEncontradaException;
import com.loja.model.ContratoAluguel;
import com.loja.model.Multa;
import com.loja.repositories.interfaces.IMultaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.time.ZoneId;

public class MultaBusiness implements IMultaBusiness {

    private static final BigDecimal VALOR_FIXO_PENALIDADE = new BigDecimal("20.00");
    private static final BigDecimal VALOR_TAXA_DIARIA = new BigDecimal("5.50");

    private final IMultaRepository multaRepository;

    public MultaBusiness(IMultaRepository multaRepository) {
        this.multaRepository = multaRepository;
    }

    public void aplicar(ContratoAluguel contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException("Não é possível aplicar multa para um contrato nulo.");
        }

        BigDecimal valorAtraso = calcularAtraso(contrato);

        if (valorAtraso.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Este contrato não possui dias de atraso para aplicação da multa.");
        }

        long dias = ChronoUnit.DAYS.between(contrato.getDataPrevDevolucao(), contrato.getDataEfetivaDevolucao());
        int diasAtraso = (int) dias;

        Multa novaMulta = new Multa(null, contrato, "Atraso na devolução do item",
                VALOR_FIXO_PENALIDADE, VALOR_TAXA_DIARIA, diasAtraso, "PENDENTE");

        multaRepository.salvar(novaMulta);
    }

    @Override
    public void quitar(String multaId) {
        if (multaId == null || multaId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da multa inválido para operação de quitação.");
        }

        Multa existente = multaRepository.buscar(multaId);

        if (existente == null) {
            throw new MultaNaoEncontradaException("Multa não encontrada para o ID: " + multaId);
        }

        existente.setStatus("QUITADA");
        multaRepository.salvar(existente);
    }

    @Override
    public Map<String, Multa> listarPorCliente(String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do cliente inválido para a consulta.");
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
    public BigDecimal calcularAtraso(ContratoAluguel contrato) {
        if (contrato == null) {
            return BigDecimal.ZERO;
        }

        LocalDate dataFinalCalculo = contrato.getDataEfetivaDevolucao() != null
                ? contrato.getDataEfetivaDevolucao()
                : LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        long diasAtraso = ChronoUnit.DAYS.between(contrato.getDataPrevDevolucao(), dataFinalCalculo);

        if (diasAtraso <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiario = VALOR_TAXA_DIARIA.multiply(BigDecimal.valueOf(diasAtraso));

        return VALOR_FIXO_PENALIDADE.add(totalDiario);
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
    public Multa buscar(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID fornecido é inválido para busca.");
        }

        Multa multa = multaRepository.buscar(id);

        if (multa == null) {
            throw new MultaNaoEncontradaException("Multa não encontrada para o Id: " + id);
        }

        return multa;
    }

    @Override
    public void atualizar(Multa multa) {
        if (multa == null) {
            throw new IllegalArgumentException("Multa inválida para atualização!");
        }

        this.buscar(multa.getId());
        multaRepository.atualizar(multa);
    }

    @Override
    public void deletarMulta(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID inválido para exclusão da multa.");
        }

        boolean deletado = multaRepository.deletar(id);

        if (!deletado) {
            throw new MultaNaoEncontradaException("Não foi possível deletar: Multa não encontrada com o ID: " + id);
        }
    }

    public void salvarDados() {
        this.multaRepository.salvarDados();
    }
}
