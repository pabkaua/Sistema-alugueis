package com.loja.repositories;
import com.loja.model.Categoria;
import com.loja.repositories.interfaces.ICategoriaRepository;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CategoriaPersistenciaCSV implements ICategoriaRepository {
    private String caminhoArquivo;
    private Map<String, Categoria> categorias;

    // construtor, inicialização do hashmap e transferencia arquivo -> hashmap
    public CategoriaPersistenciaCSV(String caminhoArquivo){
        this.caminhoArquivo = caminhoArquivo;
        this.categorias = new HashMap<>();
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
    public void salvar(Categoria categoria) {
        categorias.put(categoria.getId().toUpperCase(), categoria);
    }

    // procura a chave id no hashmap, retorna null caso não encontrar
    @Override
    public Categoria buscar(String id) {
        return categorias.get(id.toUpperCase());
    }

    // retorna o hashmap completo. obs: .unmodifiablemap() para evitar acessos por referência
    @Override
    public Map<String, Categoria> listar() {
        return Collections.unmodifiableMap(this.categorias);
    }

    // atualiza o map com base no objeto, utiliza a estrategia de sobreescrita do .put()
    @Override
    public boolean atualizar(Categoria categoria) {
        if (this.categorias.containsKey(categoria.getId())){
            categorias.put(categoria.getId(), categoria);
            return true;
        }
        return false;
    }

    // deleta um objeto do map caso exista e retorna false caso não
    @Override
    public boolean deletar(String id) {
        if(this.categorias.containsKey(id)){
            this.categorias.remove(id);
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

                if (dados.length >= 3){
                    String id = dados[0].toUpperCase();
                    String nome = dados[1];
                    boolean historico = Boolean.parseBoolean(dados[2]);

                    // criando o objeto item e adicionando no map
                    Categoria categoria = new Categoria(id, nome);
                    categoria.setHistorico(historico);

                    this.categorias.put(categoria.getId(), categoria);
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
            escritor.write("id;nome;historico");
            escritor.newLine();

            for(Categoria categoria : this.categorias.values()){
                String linha = categoria.getId().toUpperCase() + ";" +
                        categoria.getNome() + ";" +
                        categoria.hasHistorico();
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
