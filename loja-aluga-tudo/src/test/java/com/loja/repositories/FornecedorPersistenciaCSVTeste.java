package com.loja.repositories;

import com.loja.model.Fornecedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FornecedorPersistenciaCSVTeste {

    @TempDir
    Path pastaTemporaria;

    private Path arquivo;
    private FornecedorPersistenciaCSV repository;

    @BeforeEach
    void setUp() throws IOException {
        arquivo = pastaTemporaria.resolve("fornecedores.csv");
        Files.writeString(arquivo, "id;nome;cnpj;telefone;historico\n");
        repository = new FornecedorPersistenciaCSV(arquivo.toString());
    }

    private Fornecedor criarFornecedor(String id, String nome) {
        return new Fornecedor(id, nome, "12.345.678/0001-90", "(81) 99999-9999");
    }

    @Test
    void deveSalvarFornecedorNaMemoria() {
        Fornecedor fornecedor = criarFornecedor("f1", "Fornecedor A");

        repository.salvar(fornecedor);

        Fornecedor resultado = repository.buscar("F1");

        assertNotNull(resultado);
        assertEquals("Fornecedor A", resultado.getNome());
        assertEquals(1, repository.listar().size());
    }

    @Test
    void deveBuscarFornecedorIgnorandoMaiusculasEMinusculas() {
        repository.salvar(criarFornecedor("F1", "Fornecedor A"));

        assertNotNull(repository.buscar("f1"));
        assertNotNull(repository.buscar("F1"));
    }

    @Test
    void deveListarFornecedores() {
        repository.salvar(criarFornecedor("F1", "Fornecedor A"));
        repository.salvar(criarFornecedor("F2", "Fornecedor B"));

        Map<String, Fornecedor> resultado = repository.listar();

        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("F1"));
        assertTrue(resultado.containsKey("F2"));
    }

    @Test
    void naoDevePermitirAlterarMapaRetornado() {
        repository.salvar(criarFornecedor("F1", "Fornecedor A"));

        Map<String, Fornecedor> resultado = repository.listar();

        assertThrows(UnsupportedOperationException.class, () -> resultado.clear());
    }

    @Test
    void deveAtualizarFornecedorExistente() {
        repository.salvar(criarFornecedor("F1", "Nome Antigo"));

        Fornecedor atualizado = criarFornecedor("F1", "Nome Atualizado");
        boolean resultado = repository.atualizar(atualizado);

        assertTrue(resultado);
        assertEquals("Nome Atualizado", repository.buscar("F1").getNome());
    }

    @Test
    void naoDeveAtualizarFornecedorInexistente() {
        Fornecedor fornecedor = criarFornecedor("F10", "Fornecedor A");

        boolean resultado = repository.atualizar(fornecedor);

        assertFalse(resultado);
    }

    @Test
    void deveDeletarFornecedorExistente() {
        repository.salvar(criarFornecedor("F1", "Fornecedor A"));

        boolean resultado = repository.deletar("F1");

        assertTrue(resultado);
        assertNull(repository.buscar("F1"));
    }

    @Test
    void naoDeveDeletarFornecedorInexistente() {
        boolean resultado = repository.deletar("F10");

        assertFalse(resultado);
    }

    @Test
    void deveCarregarDadosDoArquivo() throws IOException {
        Files.writeString(
                arquivo,
                "id;nome;cnpj;telefone;historico\n" +
                "F1;Fornecedor A;12345678000190;81999999999;true\n"
        );

        repository = new FornecedorPersistenciaCSV(arquivo.toString());

        Fornecedor resultado = repository.buscar("F1");

        assertNotNull(resultado);
        assertEquals("Fornecedor A", resultado.getNome());
        assertEquals("12345678000190", resultado.getCnpj());
        assertTrue(resultado.hasHistorico());
    }

    @Test
    void deveIgnorarLinhaInvalidaDoArquivo() throws IOException {
        Files.writeString(
                arquivo,
                "id;nome;cnpj;telefone;historico\n" +
                "linha;invalida\n" +
                "F1;Fornecedor A;123;999;false\n"
        );

        repository = new FornecedorPersistenciaCSV(arquivo.toString());

        assertEquals(1, repository.listar().size());
        assertNotNull(repository.buscar("F1"));
    }

    @Test
    void deveSalvarDadosNoArquivo() throws IOException {
        Fornecedor fornecedor = criarFornecedor("F1", "Fornecedor A");
        fornecedor.setHistorico(true);

        repository.salvar(fornecedor);
        repository.salvarDados();

        List<String> linhas = Files.readAllLines(arquivo);

        assertEquals("id;nome;cnpj;telefone;historico", linhas.get(0));
        assertTrue(linhas.stream().anyMatch(linha ->
                linha.equals("F1;Fornecedor A;12.345.678/0001-90;(81) 99999-9999;true")
        ));
    }

    @Test
    void deveLancarExcecaoQuandoArquivoNaoExiste() {
        Path arquivoInexistente = pastaTemporaria.resolve("inexistente.csv");

        assertThrows(
                RuntimeException.class,
                () -> new FornecedorPersistenciaCSV(arquivoInexistente.toString())
        );
    }

    @Test
    void deveAlterarCaminhoDoArquivo() {
        String novoCaminho = pastaTemporaria.resolve("novo.csv").toString();

        repository.setCaminhoArquivo(novoCaminho);

        assertEquals(novoCaminho, repository.getCaminhoArquivo());
    }
}