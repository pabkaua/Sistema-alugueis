package com.loja.business;

import com.loja.model.Categoria;
import com.loja.repositories.CategoriaRepositoryFake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaBusinessTeste {

    private CategoriaBusiness business;
    private CategoriaRepositoryFake repository;

    @BeforeEach
    void setUp() {
        repository = new CategoriaRepositoryFake();
        business = new CategoriaBusiness(repository);
    }

    private Categoria criarCategoria(String id, String nome) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        return categoria;
    }

    @Test
    void deveCadastrarCategoria() {
        Categoria categoria = criarCategoria("1", "Eletrônicos");

        business.cadastrar(categoria);

        assertEquals(categoria, repository.buscar("1"));
        assertEquals(1, repository.listar().size());
    }

    @Test
    void naoDeveCadastrarCategoriaNula() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(null)
        );

        assertEquals(
                "Não foi possivel cadastrar o objeto categoria!",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComIdDuplicado() {
        business.cadastrar(criarCategoria("1", "Eletrônicos"));

        Categoria duplicada = criarCategoria("1", "Roupas");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(duplicada)
        );

        assertEquals(
                "Já existe outra categoria com esse ID: Eletrônicos",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComNomeInvalido() {
        Categoria categoria = criarCategoria("1", " ");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(categoria)
        );

        assertEquals(
                "Não foi possível cadastrar a categoria: Falha no nome",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComIdInvalido() {
        Categoria categoria = criarCategoria(" ", "Eletrônicos");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(categoria)
        );

        assertEquals(
                "Não foi possível cadastrar a categoria: Falha no ID",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComNomeDuplicado() {
        business.cadastrar(criarCategoria("1", "Eletrônicos"));

        Categoria duplicada = criarCategoria("2", "ELETRÔNICOS");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.cadastrar(duplicada)
        );

        assertEquals(
                "Já existe uma categoria com o nome: ELETRÔNICOS",
                exception.getMessage()
        );
    }

    @Test
    void deveBuscarCategoria() {
        Categoria categoria = criarCategoria("1", "Eletrônicos");
        repository.salvar(categoria);

        Categoria resultado = business.buscar("1");

        assertEquals(categoria, resultado);
    }

    @Test
    void naoDeveBuscarCategoriaInexistente() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.buscar("10")
        );

        assertEquals(
                "Categoria não encontrada!",
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
                "Id da categoria inválido",
                exception.getMessage()
        );
    }

    @Test
    void deveListarCategorias() {
        business.cadastrar(criarCategoria("1", "Eletrônicos"));
        business.cadastrar(criarCategoria("2", "Roupas"));

        Map<String, Categoria> resultado = business.listar();

        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("1"));
        assertTrue(resultado.containsKey("2"));
    }

    @Test
    void deveAtualizarCategoria() {
        repository.salvar(criarCategoria("1", "Eletrônicos"));

        Categoria atualizada = criarCategoria("1", "Informática");

        business.atualizar(atualizada);

        assertEquals(
                "Informática",
                repository.buscar("1").getNome()
        );
    }

    @Test
    void naoDeveAtualizarCategoriaNula() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.atualizar(null)
        );

        assertEquals(
                "Categoria inválida!",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveAtualizarCategoriaComNomeInvalido() {
        Categoria categoria = criarCategoria("1", " ");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.atualizar(categoria)
        );

        assertEquals(
                "Nome da categoria inválido!",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveAtualizarCategoriaInexistente() {
        Categoria categoria = criarCategoria("10", "Eletrônicos");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.atualizar(categoria)
        );

        assertEquals(
                "Não foi possível atualizar!",
                exception.getMessage()
        );
    }

    @Test
    void deveDeletarCategoriaSemHistorico() {
        Categoria categoria = criarCategoria("1", "Eletrônicos");
        repository.salvar(categoria);

        business.deletar("1");

        assertNull(repository.buscar("1"));
    }

    @Test
    void naoDeveDeletarCategoriaInexistente() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.deletar("10")
        );

        assertEquals(
                "Categoria não encontrada: 10",
                exception.getMessage()
        );
    }

    @Test
    void naoDeveDeletarCategoriaComHistorico() {
        Categoria categoria = criarCategoria("1", "Eletrônicos");
        categoria.setHistorico(true);
        repository.salvar(categoria);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> business.deletar("1")
        );

        assertEquals(
                "A categoria não pode ser excluida, tem histórico",
                exception.getMessage()
        );

        assertNotNull(repository.buscar("1"));
    }
}