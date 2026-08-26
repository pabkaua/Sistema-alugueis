package com.loja.repositories;
import com.loja.model.Categoria;
import com.loja.model.Fornecedor;
import com.loja.model.Item;
import com.loja.repositories.interfaces.IItemRepository;

import java.io.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemPersistenciaCSV implements IItemRepository {
    private String caminhoArquivo;
    private Map<String, Item> itens;

    // construtor, inicialização do hashmap e transferencia arquivo -> hashmap
    public ItemPersistenciaCSV(String caminhoArquivo){
        this.caminhoArquivo = caminhoArquivo;
        this.itens = new HashMap<>();
        this.carregarDados();
    }

    // get e set do caminho
    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }
    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    // adiciona o objeto com sua chave (seu id)
    @Override
    public void salvar(Item item) {
        itens.put(item.getId().toUpperCase(), item);
    }

    // procura a chave id no hashmap, retorna null caso não encontrar
    @Override
    public Item buscar(String id) {
        return itens.get(id.toUpperCase());
    }

    // retorna o hashmap completo. obs: .unmodifiablemap() para evitar acessos por referência
    @Override
    public Map<String, Item> listar() {
        return Collections.unmodifiableMap(this.itens);
    }


    // lista os itens com base em status, categoria ou fornecedor
    @Override
    public Map<String, Item> listar(String status) {
        return this.itens.entrySet().stream()
                // filtra os itens por status (ignorando miuscula e minuscula)
                .filter(valorfiltrado -> valorfiltrado.getValue().getStatus().equalsIgnoreCase(status))
                // "coleciona" os resultados em um Map<String, Item>, por padrão já é hashmap
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Item> listar(Categoria categoria) {
        return this.itens.entrySet().stream()
                // filtra os itens por categoria com base no id
                .filter(valorfiltrado -> valorfiltrado.getValue().getCategoria().getId().equals(categoria.getId()))
                // "coleciona" os resultados em um Map<String, Item>, por padrão já é hashmap
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Item> listar(Fornecedor fornecedor) {
        return this.itens.entrySet().stream()
                // filtra os itens por fornecedor com base no id
                .filter(valorfiltrado -> valorfiltrado.getValue().getFornecedor().getId().equals(fornecedor.getId()))
                // "coleciona" os resultados em um Map<String, Item>, por padrão já é hashmap
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    // atualiza o map com base no objeto, utiliza a estrategia de sobreescrita do .put()
    @Override
    public boolean atualizar(Item item) {
        if (this.itens.containsKey(item.getId())){
            itens.put(item.getId(), item);
            return true;
        }
        return false;
    }

    // deleta um objeto do map caso exista e retorna false caso não
    @Override
    public boolean deletar(String id) {
        if(this.itens.containsKey(id)){
            this.itens.remove(id);
            return true;
        }
        return false;
    }

    // pega cada linha do csv e cria o objeto, passando pra o map
    @Override
    public void carregarDados() {
        BufferedReader leitor = null;
        try {
            leitor = new BufferedReader(new FileReader(this.caminhoArquivo));
            String linha = leitor.readLine();

            if(linha != null && linha.toLowerCase().startsWith("id;nome")){
                linha = leitor.readLine();
            }

            while (linha != null){
                String[] dados = linha.split(";");

                if (dados.length >= 8){
                    String id = dados[0].toUpperCase();
                    String nome = dados[1];
                    BigDecimal taxaDiaria = new BigDecimal(dados[2]);
                    BigDecimal valorReposicao = new BigDecimal(dados[3]);
                    String status = dados[4];

                    /*
                    * para evitar problemas de duplicação de dados de fornecedor e categoria
                    * (ter no item.csv e fornecedor.csv, por exemplo), criamos os objetos vazios
                    * e preenchemos apenas o ID, para na facade ele criar o fornecedor e categoria
                    * e atualizar o objeto corretamente
                    */

                    Categoria categoria = new Categoria();
                    categoria.setId(dados[5].toUpperCase());
                    Fornecedor fornecedor = new Fornecedor();
                    fornecedor.setId(dados[6].toUpperCase());

                    boolean historico = Boolean.parseBoolean(dados[7]);

                    // criando o objeto item e adicionando no map
                    Item item = new Item(id, nome, taxaDiaria, valorReposicao, status, categoria, fornecedor);
                    item.setHistorico(historico);

                    this.itens.put(item.getId(), item);
                }
                linha = leitor.readLine();
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        } finally {
            if(leitor != null) {
                try {
                    leitor.close();
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void salvarDados() {
        BufferedWriter escritor = null;
        try{
            escritor = new BufferedWriter(new FileWriter(this.caminhoArquivo));
            escritor.write("id;nome;taxaDiaria;valorReposicao;status;categoriaId;fornecedorId;historico");
            escritor.newLine();

            for(Item item : this.itens.values()){
                String linha = item.getId().toUpperCase() + ";" +
                                item.getNome() + ";" +
                                item.getTaxaDiaria() + ";" +
                                item.getValorReposicao() + ";" +
                                item.getStatus() + ";" +
                                item.getCategoria().getId().toUpperCase() + ";" +
                                item.getFornecedor().getId().toUpperCase() + ";" +
                                item.hasHistorico();
                escritor.write(linha);
                escritor.newLine();
            }

        } catch (IOException e){
            throw new RuntimeException(e);
        } finally {
            if(escritor != null) {
                try {
                    escritor.close();
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
