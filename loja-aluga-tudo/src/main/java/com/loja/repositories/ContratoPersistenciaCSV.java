package com.loja.repositories;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.repositories.interfaces.IContratoRepository;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ContratoPersistenciaCSV implements IContratoRepository {

    private String caminhoArquivo;
    private Map<String, ContratoAluguel> contratos;

    public ContratoPersistenciaCSV(String caminhoArquivo){
        this.caminhoArquivo = caminhoArquivo;
        this.contratos = new HashMap<>();
        this.carregarDados();
    }

    @Override
    public void salvar(ContratoAluguel contrato) {
        contratos.put(contrato.getId().toUpperCase(), contrato);
    }

    @Override
    public ContratoAluguel buscar(String id) {
        return contratos.get(id.toUpperCase());
    }

    @Override
    public Map<String, ContratoAluguel> listar() {
        return Collections.unmodifiableMap(contratos);
    }

    @Override
    public Map<String, ContratoAluguel> listar(Cliente cliente) {
        return this.contratos.entrySet().stream()
                .filter(valorFiltrado -> valorFiltrado.getValue().getCliente().equals(cliente))
                .collect(Collectors.toMap(Map.Entry::getKey, valorFiltrado -> valorFiltrado.getValue()));
    }

    @Override
    public Map<String, ContratoAluguel> listar(String status) {
        return this.contratos.entrySet().stream()
                .filter(valorFiltrado -> valorFiltrado.getValue().getStatus().equals(status))
                .collect(Collectors.toMap(Map.Entry::getKey, valorFiltrado -> valorFiltrado.getValue()));
    }

    @Override
    public boolean atualizar(ContratoAluguel contrato) {
        if (this.contratos.containsKey(contrato.getId())) {
            contratos.put(contrato.getId(), contrato);
            return true;
        }
        return false;
    }

    @Override
    public boolean deletar(String id) {
        if (this.contratos.containsKey(id)) {
            this.contratos.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void carregarDados() {
        try (BufferedReader leitor = new BufferedReader(new FileReader(this.caminhoArquivo))) {
            String linha = leitor.readLine();

            if (linha != null && linha.toLowerCase().startsWith("id;cliente")) {
                linha = leitor.readLine();
            }

            while (linha != null) {
                String[] dados = linha.split(";");

                if (dados.length >= 9) {
                    String id = dados[0].toUpperCase();
                    String clienteId = dados[1].toUpperCase();
                    String itemId = dados[2].toUpperCase();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    LocalDate dataRetirada = LocalDate.parse(dados[3], formatter);
                    LocalDate dataPrevDevolucao = LocalDate.parse(dados[4], formatter);
                    LocalDate dataEfetivaDevolucao = dados[5].isBlank() ? null : LocalDate.parse(dados[5], formatter);
                    BigDecimal valorTotal = new BigDecimal(dados[6]);
                    String status = dados[7];
                    boolean historico = Boolean.parseBoolean(dados[8]);

                    Cliente cliente = new Cliente();
                    cliente.setId(clienteId);
                    Item item = new Item();
                    item.setId(itemId);

                    ContratoAluguel contrato = new ContratoAluguel(
                            id, cliente, item,
                            dataRetirada, dataPrevDevolucao,
                            dataEfetivaDevolucao, valorTotal, status
                    );
                    contrato.setHistorico(historico);

                    this.contratos.put(contrato.getId(), contrato);
                }
                linha = leitor.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void salvarDados() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(this.caminhoArquivo))) {
            escritor.write("id;clienteId;itemId;dataRetirada;dataPrevDevolucao;dataEfetivaDevolucao;valorTotal;status;historico");
            escritor.newLine();

            DateTimeFormatter data = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (ContratoAluguel contrato : this.contratos.values()) {
                String strRetirada = contrato.getDataRetirada().format(data);
                String strPrevDevolucao = contrato.getDataPrevDevolucao().format(data);

                String strEfetivaDevolucao = "";
                if (contrato.getDataEfetivaDevolucao() != null) {
                    strEfetivaDevolucao = contrato.getDataEfetivaDevolucao().format(data);
                }

                String linhas = contrato.getId() + ";"
                        + contrato.getCliente().getId().toUpperCase() + ";"
                        + contrato.getItem().getId().toUpperCase() + ";"
                        + strRetirada + ";"
                        + strPrevDevolucao + ";"
                        + strEfetivaDevolucao + ";"
                        + contrato.getValorTotal() + ";"
                        + contrato.getStatus() + ";"
                        + contrato.getHistorico() + ";";

                escritor.write(linhas);
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
