package com.loja.repositories;

import com.loja.model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaPersistenciaCSVTeste {

    @TempDir
    Path pastaTemporaria;

    private Path arquivo;
    private CategoriaPersistenciaCSV repository;

    @BeforeEach
    void setUp() throws IOException {
        arquivo = pastaTemporaria.resolve("categorias.csv");
        Files.writeString(arquivo, "id;nome;historico\n");
        repository = new CategoriaPersistenciaCSV(arquivo.toString());
    }

    private Categoria criarCategoria(String id, String nome) {
        return new Categoria(id, nome);
    }

    @Test
    void deveSalvarCategoriaNaMemoria() {
        Categoria categoria = criarCategoria("c1", "Eletrônicos");

        repository.salvar(categoria);

        Categoria resultado = repository.buscar("C1");

        assertNotNull(resultado);
        assertEquals("Eletrônicos", resultado.getNome());
        assertEquals(1, repository.listar().size());
    }

    @Test
    void deveBuscarCategoriaIgnorandoMaiusculasEMinusculas() {
        repository.salvar(criarCategoria("C1", "Eletrônicos"));

        assertNotNull(repository.buscar("c1"));
        assertNotNull(repository.buscar("C1"));
    }

    @Test
    void deveListarCategorias() {
        repository.salvar(criarCategoria("C1", "Eletrônicos"));
        repository.salvar(criarCategoria("C2", "Ferramentas"));

        Map<String, Categoria> resultado = repository.listar();

        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("C1"));
        assertTrue(resultado.containsKey("C2"));
    }

    @Test
    void naoDevePermitirAlterarMapaRetornado() {
        repository.salvar(criarCategoria("C1", "Eletrônicos"));

        Map<String, Categoria> resultado = repository.listar();

        assertThrows(UnsupportedOperationException.class, resultado::clear);
    }

    @Test
    void deveAtualizarCategoriaExistente() {
        repository.salvar(criarCategoria("C1", "Nome Antigo"));

        Categoria atualizada = criarCategoria("C1", "Nome Atualizado");
        boolean resultado = repository.atualizar(atualizada);

        assertTrue(resultado);
        assertEquals("Nome Atualizado", repository.buscar("C1").getNome());
    }

    @Test
    void naoDeveAtualizarCategoriaInexistente() {
        Categoria categoria = criarCategoria("C10", "Eletrônicos");

        boolean resultado = repository.atualizar(categoria);

        assertFalse(resultado);
    }

    @Test
    void deveDeletarCategoriaExistente() {
        repository.salvar(criarCategoria("C1", "Eletrônicos"));

        boolean resultado = repository.deletar("C1");

        assertTrue(resultado);
        assertNull(repository.buscar("C1"));
    }

    @Test
    void naoDeveDeletarCategoriaInexistente() {
        boolean resultado = repository.deletar("C10");

        assertFalse(resultado);
    }

    @Test
    void deveCarregarDadosDoArquivo() throws IOException {
        Files.writeString(arquivo,
                "id;nome;historico\n" +
                "C1;Eletrônicos;true\n");

        repository = new CategoriaPersistenciaCSV(arquivo.toString());

        Categoria resultado = repository.buscar("C1");

        assertNotNull(resultado);
        assertEquals("Eletrônicos", resultado.getNome());
        assertTrue(resultado.hasHistorico());
    }

    @Test
    void deveCarregarCategoriaSemHistorico() throws IOException {
        Files.writeString(arquivo,
                "id;nome;historico\n" +
                "C1;Eletrônicos;false\n");

        repository = new CategoriaPersistenciaCSV(arquivo.toString());

        Categoria resultado = repository.buscar("C1");

        assertNotNull(resultado);
        assertFalse(resultado.hasHistorico());
    }

    @Test
    void deveIgnorarLinhaInvalidaDoArquivo() throws IOException {
        Files.writeString(arquivo,
                "id;nome;historico\n" +
                "linha;invalida\n" +
                "C1;Eletrônicos;false\n");

        repository = new CategoriaPersistenciaCSV(arquivo.toString());

        assertEquals(1, repository.listar().size());
        assertNotNull(repository.buscar("C1"));
    }

    @Test
    void deveSalvarDadosNoArquivo() throws IOException {
        Categoria categoria = criarCategoria("C1", "Eletrônicos");
        categoria.setHistorico(true);

        repository.salvar(categoria);
        repository.salvarDados();

        List<String> linhas = Files.readAllLines(arquivo);

        assertEquals("id;nome;historico", linhas.get(0));
        assertTrue(linhas.contains("C1;Eletrônicos;true"));
    }

    @Test
    void deveLancarExcecaoQuandoArquivoNaoExiste() {
        Path arquivoInexistente = pastaTemporaria.resolve("inexistente.csv");

        assertThrows(RuntimeException.class,
                () -> new CategoriaPersistenciaCSV(arquivoInexistente.toString()));
    }

    @Test
    void deveAlterarCaminhoDoArquivo() {
        String novoCaminho = pastaTemporaria.resolve("novo.csv").toString();

        repository.setCaminhoArquivo(novoCaminho);

        assertEquals(novoCaminho, repository.getCaminhoArquivo());
    }
}