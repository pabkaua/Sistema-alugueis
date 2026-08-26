package com.loja.repositories;

import com.loja.model.Administrador;
import com.loja.model.Cliente;
import com.loja.model.Funcionario;
import com.loja.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioPersistenciaCSVTeste {

    @TempDir
    Path tempDir;

    private Path arquivoCsv;

    @BeforeEach
    void setUp() {
        arquivoCsv = tempDir.resolve("usuarios.csv");
    }

    private UsuarioPersistenciaCSV criarRepositorioComArquivoNovo() throws IOException {
        Files.writeString(arquivoCsv, "id;nome;login;senha;perfil;ativo;campoExtra1;campoExtra2\n");
        return new UsuarioPersistenciaCSV(arquivoCsv.toString());
    }

    @Test
    @DisplayName("salvar: deve manter usuário em memória e disponível para busca")
    void salvar_deveManterUsuarioEmMemoria() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        Cliente cliente = new Cliente("1", "João", "joao@email.com", "123");

        repo.salvar(cliente);

        assertNotNull(repo.buscar("1"));
        assertEquals("João", repo.buscar("1").getNome());
    }

    @Test
    @DisplayName("buscar: deve retornar null quando id não existe")
    void buscar_deveRetornarNull_quandoIdNaoExiste() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();

        assertNull(repo.buscar("inexistente"));
    }

    @Test
    @DisplayName("buscarPorEmail: deve encontrar usuário ignorando maiúsculas/minúsculas")
    void buscarPorEmail_deveIgnorarCase() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));

        Usuario resultado = repo.buscarPorEmail("JOAO@EMAIL.COM");

        assertNotNull(resultado);
        assertEquals("1", resultado.getId());
    }

    @Test
    @DisplayName("buscarPorEmail: deve retornar null quando email não existe")
    void buscarPorEmail_deveRetornarNull_quandoEmailNaoExiste() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();

        assertNull(repo.buscarPorEmail("naoexiste@email.com"));
    }

    @Test
    @DisplayName("atualizar: deve substituir usuário existente e retornar true")
    void atualizar_deveSubstituir_quandoUsuarioExiste() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));

        boolean resultado = repo.atualizar(new Cliente("1", "João Novo", "novo@email.com", "456"));

        assertTrue(resultado);
        assertEquals("João Novo", repo.buscar("1").getNome());
        assertEquals("novo@email.com", repo.buscar("1").getLogin());
    }

    @Test
    @DisplayName("atualizar: deve retornar false quando usuário não existe")
    void atualizar_deveRetornarFalse_quandoUsuarioNaoExiste() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();

        boolean resultado = repo.atualizar(new Cliente("99", "X", "x@email.com", "x"));

        assertFalse(resultado);
    }

    @Test
    @DisplayName("deletar: deve remover usuário existente e retornar true")
    void deletar_deveRemover_quandoUsuarioExiste() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));

        boolean resultado = repo.deletar("1");

        assertTrue(resultado);
        assertNull(repo.buscar("1"));
    }

    @Test
    @DisplayName("deletar: deve retornar false quando usuário não existe")
    void deletar_deveRetornarFalse_quandoUsuarioNaoExiste() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();

        assertFalse(repo.deletar("inexistente"));
    }

    @Test
    @DisplayName("listar: deve retornar todos os usuários cadastrados")
    void listar_deveRetornarTodos() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));
        repo.salvar(new Administrador("2", "Carlos", "carlos@email.com", "abc", 1, "TI"));

        Map<String, Usuario> resultado = repo.listar();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("listar: mapa retornado deve ser somente leitura")
    void listar_deveRetornarMapaImutavel() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));

        Map<String, Usuario> resultado = repo.listar();

        assertThrows(UnsupportedOperationException.class,
                () -> resultado.put("2", new Cliente("2", "Maria", "maria@email.com", "456")));
    }

    @Test
    @DisplayName("listar(perfil): deve filtrar apenas o perfil solicitado")
    void listarPorPerfil_deveFiltrarCorretamente() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));
        repo.salvar(new Funcionario("2", "Ana", "ana@email.com", "456", "Caixa"));

        Map<String, Usuario> resultado = repo.listar("CLIENTE");

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey("1"));
    }

    @Test
    @DisplayName("salvarDados + carregarDados: Cliente deve preservar campo inadimplente")
    void salvarEcarregar_clienteDevePreservarInadimplente() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        Cliente cliente = new Cliente("1", "João", "joao@email.com", "123");
        cliente.setInadimplente(true);
        cliente.setAtivo(true);
        repo.salvar(cliente);

        repo.salvarDados();
        UsuarioPersistenciaCSV repoRecarregado = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        Usuario recarregado = repoRecarregado.buscar("1");
        assertInstanceOf(Cliente.class, recarregado);
        assertTrue(((Cliente) recarregado).isInadimplente());
        assertTrue(recarregado.isAtivo());
    }

    @Test
    @DisplayName("salvarDados + carregarDados: Administrador deve preservar nivelAcesso e departamento")
    void salvarEcarregar_administradorDevePreservarCampos() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Administrador("1", "Carlos", "carlos@email.com", "abc", 5, "Financeiro"));

        repo.salvarDados();
        UsuarioPersistenciaCSV repoRecarregado = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        Usuario recarregado = repoRecarregado.buscar("1");
        assertInstanceOf(Administrador.class, recarregado);
        Administrador admin = (Administrador) recarregado;
        assertEquals(5, admin.getNivelAcesso());
        assertEquals("Financeiro", admin.getDepartamento());
    }

    @Test
    @DisplayName("salvarDados + carregarDados: Funcionario deve preservar cargo")
    void salvarEcarregar_funcionarioDevePreservarCargo() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Funcionario("1", "Ana", "ana@email.com", "456", "Gerente"));

        repo.salvarDados();
        UsuarioPersistenciaCSV repoRecarregado = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        Usuario recarregado = repoRecarregado.buscar("1");
        assertInstanceOf(Funcionario.class, recarregado);
        assertEquals("Gerente", ((Funcionario) recarregado).getCargo());
    }

    @Test
    @DisplayName("salvarDados + carregarDados: deve preservar múltiplos usuários de perfis diferentes")
    void salvarEcarregar_devePreservarMultiplosUsuarios() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));
        repo.salvar(new Administrador("2", "Carlos", "carlos@email.com", "abc", 2, "TI"));
        repo.salvar(new Funcionario("3", "Ana", "ana@email.com", "456", "Caixa"));

        repo.salvarDados();
        UsuarioPersistenciaCSV repoRecarregado = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        assertEquals(3, repoRecarregado.listar().size());
        assertInstanceOf(Cliente.class, repoRecarregado.buscar("1"));
        assertInstanceOf(Administrador.class, repoRecarregado.buscar("2"));
        assertInstanceOf(Funcionario.class, repoRecarregado.buscar("3"));
    }

    @Test
    @DisplayName("carregarDados: deve ignorar linhas em branco no arquivo")
    void carregarDados_deveIgnorarLinhasEmBranco() throws IOException {
        String conteudo = "id;nome;login;senha;perfil;ativo;campoExtra1;campoExtra2\n"
                + "1;João;joao@email.com;123;CLIENTE;true;false;\n"
                + "\n"
                + "2;Ana;ana@email.com;456;FUNCIONARIO;true;Caixa;\n";
        Files.writeString(arquivoCsv, conteudo);

        UsuarioPersistenciaCSV repo = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        assertEquals(2, repo.listar().size());
    }

    @Test
    @DisplayName("carregarDados: deve ignorar linhas com menos de 6 colunas")
    void carregarDados_deveIgnorarLinhasIncompletas() throws IOException {
        String conteudo = "id;nome;login;senha;perfil;ativo;campoExtra1;campoExtra2\n"
                + "1;João;joao@email.com;123;CLIENTE\n" // faltando "ativo"
                + "2;Ana;ana@email.com;456;FUNCIONARIO;true;Caixa;\n";
        Files.writeString(arquivoCsv, conteudo);

        UsuarioPersistenciaCSV repo = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        assertEquals(1, repo.listar().size());
        assertNull(repo.buscar("1"));
        assertNotNull(repo.buscar("2"));
    }

    @Test
    @DisplayName("carregarDados: Administrador sem campos extras deve usar valores padrão")
    void carregarDados_administradorSemCamposExtras_deveUsarPadrao() throws IOException {
        String conteudo = "id;nome;login;senha;perfil;ativo\n"
                + "1;Carlos;carlos@email.com;abc;ADMINISTRADOR;true\n";
        Files.writeString(arquivoCsv, conteudo);

        UsuarioPersistenciaCSV repo = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        Administrador admin = (Administrador) repo.buscar("1");
        assertNotNull(admin);
        assertEquals(1, admin.getNivelAcesso());
        assertEquals("Geral", admin.getDepartamento());
    }

    @Test
    @DisplayName("carregarDados: deve ignorar perfil desconhecido")
    void carregarDados_deveIgnorarPerfilDesconhecido() throws IOException {
        String conteudo = "id;nome;login;senha;perfil;ativo\n"
                + "1;Desconhecido;x@email.com;123;GERENTE;true\n";
        Files.writeString(arquivoCsv, conteudo);

        UsuarioPersistenciaCSV repo = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        assertTrue(repo.listar().isEmpty());
    }

    @Test
    @DisplayName("carregarDados: não deve lançar exceção quando arquivo não existe")
    void carregarDados_naoDeveLancarExcecao_quandoArquivoNaoExiste() {
        Path arquivoInexistente = tempDir.resolve("nao-existe.csv");

        UsuarioPersistenciaCSV repo = assertDoesNotThrow(
                () -> new UsuarioPersistenciaCSV(arquivoInexistente.toString()));

        assertTrue(repo.listar().isEmpty());
    }

    @Test
    @DisplayName("salvarDados: deve escrever cabeçalho e uma linha por usuário no arquivo")
    void salvarDados_deveEscreverArquivoCorretamente() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));

        repo.salvarDados();

        List<String> linhas = Files.readAllLines(arquivoCsv);
        assertEquals(2, linhas.size());
        assertTrue(linhas.get(0).startsWith("id;nome;login;senha;perfil;ativo"));
        assertTrue(linhas.get(1).startsWith("1;João;joao@email.com;123;CLIENTE;"));
    }

    @Test
    @DisplayName("salvarDados: deve refletir remoções feitas em memória")
    void salvarDados_deveRefletirDelecao() throws IOException {
        UsuarioPersistenciaCSV repo = criarRepositorioComArquivoNovo();
        repo.salvar(new Cliente("1", "João", "joao@email.com", "123"));
        repo.salvar(new Cliente("2", "Maria", "maria@email.com", "456"));
        repo.deletar("1");

        repo.salvarDados();
        UsuarioPersistenciaCSV repoRecarregado = new UsuarioPersistenciaCSV(arquivoCsv.toString());

        assertEquals(1, repoRecarregado.listar().size());
        assertNull(repoRecarregado.buscar("1"));
        assertNotNull(repoRecarregado.buscar("2"));
    }
}