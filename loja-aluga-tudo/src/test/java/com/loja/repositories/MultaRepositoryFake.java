package com.loja.repositories;

import com.loja.model.Cliente;
import com.loja.model.Multa;
import com.loja.repositories.interfaces.IMultaRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MultaRepositoryFake implements IMultaRepository {
    private final Map<String, Multa> multas = new HashMap<>();

    @Override
    public void salvar(Multa multa) {
        if (multa.getId() == null) {
            long maiorId = multas.keySet().stream()
                    .filter(id -> id != null && id.matches("\\d+"))
                    .mapToLong(Long::parseLong)
                    .max()
                    .orElse(0);
            multa.setId(String.valueOf(maiorId + 1));
        }
        multas.put(multa.getId(), multa);
    }

    @Override
    public Multa buscar(String id) {
        return multas.get(id);
    }

    @Override
    public Map<String, Multa> listar() {
        return Collections.unmodifiableMap(multas);
    }

    @Override
    public Map<String, Multa> listar(Cliente cliente) {
        return multas.entrySet().stream()
                .filter(entry -> entry.getValue().getContrato() != null 
                        && entry.getValue().getContrato().getCliente() != null 
                        && entry.getValue().getContrato().getCliente().getId().equals(cliente.getId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Multa> listar(String status) {
        return multas.entrySet().stream()
                .filter(entry -> entry.getValue().getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean atualizar(Multa multa) {
        if (multas.containsKey(multa.getId())) {
            multas.put(multa.getId(), multa);
            return true;
        }
        return false;
    }

    @Override
    public boolean deletar(String id) {
        return multas.remove(id) != null;
    }

    @Override public void carregarDados() {}
    @Override public void salvarDados() {}
}