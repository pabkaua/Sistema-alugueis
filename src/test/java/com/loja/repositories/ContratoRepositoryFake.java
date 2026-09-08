package com.loja.repositories;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.repositories.interfaces.IContratoRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ContratoRepositoryFake implements IContratoRepository {

    private Map<String, ContratoAluguel> contratos = new HashMap<>();

    @Override
    public void salvar(ContratoAluguel contrato) {
        contratos.put(contrato.getId(), contrato);
    }

    @Override
    public ContratoAluguel buscar(String id) {
        return contratos.get(id);
    }

    @Override
    public Map<String, ContratoAluguel> listar() {
        return Collections.unmodifiableMap(contratos);
    }

    @Override
    public Map<String, ContratoAluguel> listar(String status) {
        return contratos.entrySet().stream()
                .filter(e -> e.getValue().getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, ContratoAluguel> listar(Cliente cliente) {
        return contratos.entrySet().stream()
                .filter(e -> e.getValue().getCliente().getId().equals(cliente.getId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean atualizar(ContratoAluguel contrato) {
        if (!contratos.containsKey(contrato.getId())) return false;
        contratos.put(contrato.getId(), contrato);
        return true;
    }

    @Override
    public boolean deletar(String id) {
        return contratos.remove(id) != null;
    }

    @Override public void carregarDados() {}

    @Override public void salvarDados() {}
}