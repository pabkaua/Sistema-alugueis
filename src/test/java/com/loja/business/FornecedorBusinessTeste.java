package com.loja.business;

import com.loja.model.Fornecedor;
import com.loja.repositories.FornecedorRepositoryFake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FornecedorBusinessTeste {

    private FornecedorBusiness business;
    private FornecedorRepositoryFake repository;

    @BeforeEach
    void setUp() {
        repository = new FornecedorRepositoryFake();
        business = new FornecedorBusiness(repository);
    }

    private Fornecedor criarFornecedor(String id, String nome) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(id);
        fornecedor.setNome(nome);
        return fornecedor;
    }

    @Test
    void deveCadastrarFornecedor() {
        Fornecedor fornecedor = criarFornecedor("1", "Fornecedor A");

        business.cadastrar(fornecedor);

        assertEquals(fornecedor, repository.buscar("1"));
        assertEquals(1, repository.listar().size());
    }

    @Test
    void naoDeveCadastrarFornecedorNulo() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(null)
        ); //expressão lambda, chama o método cadastrar com null

        assertEquals(
                "Não foi possivel cadastrar o objeto fornecedor!",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarFornecedorComIdDuplicado() {
        business.cadastrar(criarFornecedor("1", "Fornecedor A"));

        Fornecedor fornecedorDuplicado = criarFornecedor("1", "Fornecedor B");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(fornecedorDuplicado)
        );

        assertEquals(
                "Já existe outro fornecedor com esse ID: Fornecedor A",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarFornecedorComNomeInvalido() {
        Fornecedor fornecedor = criarFornecedor("1", " ");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(fornecedor)
        );

        assertEquals(
                "Não foi possível cadastrar o fornecedor: Falha no nome",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarFornecedorComIdInvalido() {
        Fornecedor fornecedor = criarFornecedor(" ", "Fornecedor A");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(fornecedor)
        );

        assertEquals(
                "Não foi possível cadastrar o fornecedor: Falha no ID",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarFornecedorComNomeDuplicado() {
        business.cadastrar(criarFornecedor("1", "Fornecedor A"));

        Fornecedor fornecedorDuplicado =
                criarFornecedor("2", "FORNECEDOR A");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(fornecedorDuplicado)
        );

        assertEquals(
                "Já existe um fornecedor com o nome: FORNECEDOR A",
                exception.getMessage()
        );
    }

    @Test
    void deveBuscarFornecedor() {
        Fornecedor fornecedor =
                criarFornecedor("1", "Fornecedor A");

        repository.salvar(fornecedor);

        Fornecedor resultado = business.buscar("1");

        assertEquals(fornecedor, resultado);
    }

    @Test
    void naoDeveBuscarFornecedorInexistente() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.buscar("10")
        );

        assertEquals(
                "Fornecedor não encontrado!",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveBuscarComIdNulo() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.buscar(null)
        );

        assertEquals(
                "Id do fornecedor inválido",
                exception.getMessage()
        );
    }

    @Test
    void deveListarFornecedores() {
        business.cadastrar(criarFornecedor("1", "Fornecedor A"));
        business.cadastrar(criarFornecedor("2", "Fornecedor B"));

        Map<String, Fornecedor> resultado = business.listar();

        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("1"));
        assertTrue(resultado.containsKey("2"));
    }

    @Test
    void deveAtualizarFornecedor() {
        Fornecedor fornecedor =
                criarFornecedor("1", "Fornecedor A");

        repository.salvar(fornecedor);

        Fornecedor atualizado =
                criarFornecedor("1", "Fornecedor Atualizado");

        business.atualizar(atualizado);

        assertEquals(
                "Fornecedor Atualizado",
                repository.buscar("1").getNome()
        );
    }

    @Test
    void naoDeveAtualizarFornecedorInexistente() {
        Fornecedor fornecedor =
                criarFornecedor("10", "Fornecedor A");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.atualizar(fornecedor)
        );

        assertEquals(
                "Não foi possível atualizar!",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveAtualizarFornecedorNulo() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.atualizar(null)
        );

        assertEquals(
                "Fornecedor inválido!",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveDeletarFornecedorInexistente() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.deletar("10")
        );

        assertEquals(
                "Fornecedor não encontrado: 10",
                exception.getMessage()
        );
    }
}