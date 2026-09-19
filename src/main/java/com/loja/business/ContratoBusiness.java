package com.loja.business;

import com.loja.business.interfaces.IContratoBusiness;
import com.loja.business.interfaces.IItemBusiness;
import com.loja.business.interfaces.IUsuarioBusiness;
import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.repositories.interfaces.IContratoRepository;
import com.loja.business.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ContratoBusiness implements IContratoBusiness {

    private static final String STATUS_ATIVO = "ATIVO";
    private IContratoRepository repo;
    private IItemBusiness itemBusiness;
    private IUsuarioBusiness usuarioBusiness;

    public ContratoBusiness(IContratoRepository repo, IItemBusiness itemBusiness, IUsuarioBusiness usuarioBusiness) {
        this.repo = repo;
        this.itemBusiness = itemBusiness;
        this.usuarioBusiness = usuarioBusiness;
    }

    @Override
    public ContratoAluguel registrarAluguel(String clienteId, String itemId, LocalDate dataRetirada, LocalDate dataPrevDevolucao) {
        Cliente cliente = (Cliente) usuarioBusiness.buscarPorId(clienteId);
        if (cliente == null) {
            throw new BusinessException("Cliente não encontrado.");
        }
        if (cliente.isInadimplente()) {
            throw new BusinessException("Cliente inadimplente. Quite as multas pendentes para realizar um novo aluguel.");
        }

        Item item = itemBusiness.buscar(itemId);
        if (item == null) {
            throw new BusinessException("Item não encontrado.");
        }
        if (!item.getStatus().equalsIgnoreCase("DISPONIVEL")) {
            throw new BusinessException("Item indisponível para aluguel. Status atual: " + item.getStatus());
        }

        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataRetirada, dataPrevDevolucao);
        BigDecimal valorTotal = item.getTaxaDiaria().multiply(BigDecimal.valueOf(dias));

        ContratoAluguel contrato = new ContratoAluguel(
                UUID.randomUUID().toString(),
                cliente,
                item,
                dataRetirada,
                dataPrevDevolucao,
                null,           // ainda não devolvido
                valorTotal,
                STATUS_ATIVO
        );

        itemBusiness.atualizar(item);

        cliente.setHistorico(true);
        usuarioBusiness.atualizar(cliente);

        repo.salvar(contrato);
        return contrato;
    }

    @Override
    public ContratoAluguel buscar(String id) {
        return repo.buscar(id);
    }

    @Override
    public ContratoAluguel processarDevolucao(String contratoId) {
        ContratoAluguel contrato = repo.buscar(contratoId);
        if (contrato == null) {
            throw new BusinessException("Contrato não encontrado.");
        }
        if (!contrato.getStatus().equalsIgnoreCase(STATUS_ATIVO)) {
            throw new BusinessException("Este contrato não está ativo.");
        }

        contrato.setDataEfetivaDevolucao(LocalDate.now());
        contrato.setStatus("ENCERRADO");

        // libera o item de volta para disponível
        Item item = contrato.getItem();
        item.setStatus("DISPONIVEL");
        itemBusiness.atualizar(item);

        repo.atualizar(contrato);
        return contrato;
    }

    @Override
    public Map<String, ContratoAluguel> listarAtivos() {
        return repo.listar(STATUS_ATIVO);
    }

    @Override
    public Map<String, ContratoAluguel> listarPorCliente(String clienteId) {
        Cliente cliente = (Cliente) usuarioBusiness.buscarPorId(clienteId);
        if (cliente == null) {
            return Collections.emptyMap();
        }
        return repo.listar(cliente);
    }

    @Override
    public Map<String, ContratoAluguel> listar() {
        return repo.listar();
    }

    @Override
    public void atualizar(ContratoAluguel contrato) {
        repo.atualizar(contrato);
    }

    public void salvarDados(){
        this.repo.salvarDados();
    }
}