package com.loja.repositories;

import com.loja.model.Categoria;
import com.loja.model.Fornecedor;
import com.loja.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ItemPersistenciaCSVTeste {

    @TempDir
    Path pastaTemporaria;

    private Path arquivo;
    private ItemPersistenciaCSV repository;

    @BeforeEach
    void setUp() throws IOException {
        arquivo = pastaTemporaria.resolve("itens.csv");
        Files.writeString(arquivo, "id;nome;taxaDiaria;valorReposicao;status;categoriaId;fornecedorId;historico\n");
        repository = new ItemPersistenciaCSV(arquivo.toString());
    }

    private Item criarItem(String id, String nome, String status, String categoriaId, String fornecedorId) {
        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(fornecedorId);

        return new Item(id, nome, new BigDecimal("10.50"), new BigDecimal("100.00"),
                status, categoria, fornecedor);
    }

    @Test
    void deveSalvarItemNaMemoria() {
        Item item = criarItem("i1", "Furadeira", "DISPONIVEL", "C1", "F1");

        repository.salvar(item);

        Item resultado = repository.buscar("I1");

        assertNotNull(resultado);
        assertEquals("Furadeira", resultado.getNome());
        assertEquals(1, repository.listar().size());
    }

    @Test
    void deveBuscarItemIgnorandoMaiusculasEMinusculas() {
        repository.salvar(criarItem("I1", "Furadeira", "DISPONIVEL", "C1", "F1"));

        assertNotNull(repository.buscar("i1"));
        assertNotNull(repository.buscar("I1"));
    }

    @Test
    void deveListarItens() {
        repository.salvar(criarItem("I1", "Furadeira", "DISPONIVEL", "C1", "F1"));
        repository.salvar(criarItem("I2", "Serra", "ALUGADO", "C2", "F2"));

        Map<String, Item> resultado = repository.listar();

        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("I1"));
        assertTrue(resultado.containsKey("I2"));
    }

    @Test
    void naoDevePermitirAlterarMapaRetornado() {
        repository.salvar(criarItem("I1", "Furadeira", "DISPONIVEL", "C1", "F1"));

        Map<String, Item> resultado = repository.listar();

        assertThrows(UnsupportedOperationException.class, resultado::clear);
    }

    @Test
    void deveListarItensPorStatus() {
        repository.salvar(criarItem("I1", "Furadeira", "DISPONIVEL", "C1", "F1"));
        repository.salvar(criarItem("I2", "Serra", "ALUGADO", "C1", "F1"));
        repository.salvar(criarItem("I3", "Martelo", "disponivel", "C2", "F2"));

        Map<String, Item> resultado = repository.listar("DISPONIVEL");

        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("I1"));
        assertTrue(resultado.containsKey("I3"));
    }

    @Test
    void deveListarItensPorCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId("C1");

        repository.salvar(criarItem("I1", "Furadeira", "DISPONIVEL", "C1", "F1"));
        repository.salvar(criarItem("I2", "Serra", "DISPONIVEL", "C2", "F1"));

        Map<String, Item> resultado = repository.listar(categoria);

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey("I1"));
    }

    @Test
    void deveListarItensPorFornecedor() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId("F1");

        repository.salvar(criarItem("I1", "Furadeira", "DISPONIVEL", "C1", "F1"));
        repository.salvar(criarItem("I2", "Serra", "DISPONIVEL", "C1", "F2"));

        Map<String, Item> resultado = repository.listar(fornecedor);

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey("I1"));
    }

    @Test
    void deveAtualizarItemExistente() {
        repository.salvar(criarItem("I1", "Nome Antigo", "DISPONIVEL", "C1", "F1"));

        Item atualizado = criarItem("I1", "Nome Atualizado", "ALUGADO", "C1", "F1");
        boolean resultado = repository.atualizar(atualizado);

        assertTrue(resultado);
        assertEquals("Nome Atualizado", repository.buscar("I1").getNome());
        assertEquals("ALUGADO", repository.buscar("I1").getStatus());
    }

    @Test
    void naoDeveAtualizarItemInexistente() {
        Item item = criarItem("I10", "Furadeira", "DISPONIVEL", "C1", "F1");

        boolean resultado = repository.atualizar(item);

        assertFalse(resultado);
    }

    @Test
    void deveDeletarItemExistente() {
        repository.salvar(criarItem("I1", "Furadeira", "DISPONIVEL", "C1", "F1"));

        boolean resultado = repository.deletar("I1");

        assertTrue(resultado);
        assertNull(repository.buscar("I1"));
    }

    @Test
    void naoDeveDeletarItemInexistente() {
        assertFalse(repository.deletar("I10"));
    }

    @Test
    void deveCarregarDadosDoArquivo() throws IOException {
        Files.writeString(arquivo,
                "id;nome;taxaDiaria;valorReposicao;status;categoriaId;fornecedorId;historico\n" +
                "I1;Furadeira;10.50;100.00;DISPONIVEL;C1;F1;true\n");

        repository = new ItemPersistenciaCSV(arquivo.toString());

        Item resultado = repository.buscar("I1");

        assertNotNull(resultado);
        assertEquals("Furadeira", resultado.getNome());
        assertEquals(new BigDecimal("10.50"), resultado.getTaxaDiaria());
        assertEquals("C1", resultado.getCategoria().getId());
        assertEquals("F1", resultado.getFornecedor().getId());
        assertTrue(resultado.hasHistorico());
    }

    @Test
    void deveIgnorarLinhaInvalidaDoArquivo() throws IOException {
        Files.writeString(arquivo,
                "id;nome;taxaDiaria;valorReposicao;status;categoriaId;fornecedorId;historico\n" +
                "linha;invalida\n" +
                "I1;Furadeira;10.50;100.00;DISPONIVEL;C1;F1;false\n");

        repository = new ItemPersistenciaCSV(arquivo.toString());

        assertEquals(1, repository.listar().size());
        assertNotNull(repository.buscar("I1"));
    }

    @Test
    void deveSalvarDadosNoArquivo() throws IOException {
        Item item = criarItem("I1", "Furadeira", "DISPONIVEL", "C1", "F1");
        item.setHistorico(true);

        repository.salvar(item);
        repository.salvarDados();

        List<String> linhas = Files.readAllLines(arquivo);

        assertEquals("id;nome;taxaDiaria;valorReposicao;status;categoriaId;fornecedorId;historico", linhas.get(0));
        assertTrue(linhas.contains("I1;Furadeira;10.50;100.00;DISPONIVEL;C1;F1;true"));
    }

    @Test
    void deveLancarExcecaoQuandoArquivoNaoExiste() {
        Path arquivoInexistente = pastaTemporaria.resolve("inexistente.csv");

        assertThrows(RuntimeException.class,
                () -> new ItemPersistenciaCSV(arquivoInexistente.toString()));
    }

    @Test
    void deveAlterarCaminhoDoArquivo() {
        String novoCaminho = pastaTemporaria.resolve("novo.csv").toString();

        repository.setCaminhoArquivo(novoCaminho);

        assertEquals(novoCaminho, repository.getCaminhoArquivo());
    }
}