package com.loja.business;

import com.loja.business.interfaces.IContratoBusiness;
import com.loja.business.interfaces.IItemBusiness;
import com.loja.business.interfaces.IUsuarioBusiness;
import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.repositories.interfaces.IContratoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ContratoBusiness implements IContratoBusiness {

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
            throw new RuntimeException("Cliente não encontrado.");
        }
        if (cliente.isInadimplente()) {
            throw new RuntimeException("Cliente inadimplente. Quite as multas pendentes para realizar um novo aluguel.");
        }

        Item item = itemBusiness.buscar(itemId);
        if (item == null) {
            throw new RuntimeException("Item não encontrado.");
        }
        if (!item.getStatus().equalsIgnoreCase("DISPONIVEL")) {
            throw new RuntimeException("Item indisponível para aluguel. Status atual: " + item.getStatus());
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
                "ATIVO"
        );

        item.setStatus("ALUGADO");
        item.setHistorico(true);
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
            throw new RuntimeException("Contrato não encontrado.");
        }
        if (!contrato.getStatus().equalsIgnoreCase("ATIVO")) {
            throw new RuntimeException("Este contrato não está ativo.");
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
        return repo.listar("ATIVO");
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