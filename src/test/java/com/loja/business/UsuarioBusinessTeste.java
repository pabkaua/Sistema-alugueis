package com.loja.business;

import com.loja.model.Administrador;
import com.loja.model.Cliente;
import com.loja.model.Funcionario;
import com.loja.model.Usuario;
import com.loja.repositories.UsuarioRepositoryFake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioBusinessTeste {

    private UsuarioRepositoryFake repositorio;
    private UsuarioBusiness business;

    @BeforeEach
    void setUp() {
        repositorio = new UsuarioRepositoryFake();
        business = new UsuarioBusiness(repositorio);
    }

    @Test
    @DisplayName("cadastrar: deve salvar usuário quando login não existe")
    void cadastrar_deveSalvar_quandoLoginNaoExiste() {
        Cliente cliente = new Cliente("1", "João", "joao@email.com", "123");

        business.cadastrar(cliente);

        assertNotNull(repositorio.buscar("1"));
    }

    @Test
    @DisplayName("cadastrar: deve lançar exceção quando login já está cadastrado")
    void cadastrar_deveLancarExcecao_quandoLoginJaExiste() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));
        Cliente duplicado = new Cliente("2", "João 2", "joao@email.com", "456");

        assertThrows(RuntimeException.class, () -> business.cadastrar(duplicado));
        assertNull(repositorio.buscar("2"));
    }

    @Test
    @DisplayName("cadastrar: deve permitir logins diferentes")
    void cadastrar_devePermitir_loginsDiferentes() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));
        business.cadastrar(new Cliente("2", "Maria", "maria@email.com", "456"));

        assertEquals(2, business.listar().size());
    }

    @Test
    @DisplayName("buscarPorId: deve retornar usuário quando id existe")
    void buscarPorId_deveRetornarUsuario_quandoIdExiste() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));

        Usuario resultado = business.buscarPorId("1");

        assertEquals("João", resultado.getNome());
    }

    @Test
    @DisplayName("buscarPorId: deve lançar exceção quando id não existe")
    void buscarPorId_deveLancarExcecao_quandoIdNaoExiste() {
        assertThrows(RuntimeException.class, () -> business.buscarPorId("99"));
    }

    @Test
    @DisplayName("buscarPorEmail: deve retornar usuário quando email existe")
    void buscarPorEmail_deveRetornarUsuario_quandoEmailExiste() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));

        Usuario resultado = business.buscarPorEmail("joao@email.com");

        assertNotNull(resultado);
        assertEquals("1", resultado.getId());
    }

    @Test
    @DisplayName("buscarPorEmail: deve lançar exceção quando email não existe")
    void buscarPorEmail_deveLancarExcecao_quandoEmailNaoExiste() {
        assertThrows(RuntimeException.class,
                () -> business.buscarPorEmail("naoexiste@email.com"));
    }

    @Test
    @DisplayName("autenticar: deve retornar usuário quando credenciais corretas")
    void autenticar_deveRetornarUsuario_quandoCredenciaisCorretas() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "senha123"));

        Usuario resultado = business.autenticar("joao@email.com", "senha123");

        assertNotNull(resultado);
        assertEquals("João", resultado.getNome());
    }

    @Test
    @DisplayName("autenticar: deve lançar exceção quando senha está errada")
    void autenticar_deveLancarExcecao_quandoSenhaErrada() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "senha123"));

        assertThrows(RuntimeException.class,
                () -> business.autenticar("joao@email.com", "senhaErrada"));
    }

    @Test
    @DisplayName("autenticar: deve lançar exceção quando email não existe")
    void autenticar_deveLancarExcecao_quandoEmailNaoExiste() {
        assertThrows(RuntimeException.class,
                () -> business.autenticar("naoexiste@email.com", "123"));
    }

    @Test
    @DisplayName("autenticar: deve ser case-insensitive no email")
    void autenticar_deveFuncionar_comEmailEmCaseDiferente() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));

        Usuario resultado = business.autenticar("JOAO@EMAIL.COM", "123");

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("atualizar: deve alterar nome, login e senha")
    void atualizar_deveAlterarDados_quandoUsuarioExiste() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));

        business.atualizar(new Cliente("1", "João Novo", "novo@email.com", "456"));

        Usuario atualizado = repositorio.buscar("1");
        assertEquals("João Novo", atualizado.getNome());
        assertEquals("novo@email.com", atualizado.getLogin());
        assertEquals("456", atualizado.getSenha());
    }

    @Test
    @DisplayName("atualizar: deve lançar exceção quando usuário não existe")
    void atualizar_deveLancarExcecao_quandoUsuarioNaoExiste() {
        assertThrows(RuntimeException.class,
                () -> business.atualizar(new Cliente("99", "X", "x@x.com", "x")));
    }

    @Test
    @DisplayName("deletar: deve remover usuário existente")
    void deletar_deveRemoverUsuario_quandoExiste() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));

        business.deletar("1");

        assertNull(repositorio.buscar("1"));
    }

    @Test
    @DisplayName("deletar: deve lançar exceção quando usuário não existe")
    void deletar_deveLancarExcecao_quandoUsuarioNaoExiste() {
        assertThrows(RuntimeException.class, () -> business.deletar("99"));
    }

    @Test
    @DisplayName("listar: deve retornar todos os usuários cadastrados")
    void listar_deveRetornarTodosOsUsuarios() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));
        business.cadastrar(new Administrador("2", "Carlos", "carlos@email.com", "abc", 1, "TI"));

        Map<String, Usuario> resultado = business.listar();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("listar: deve retornar mapa vazio quando não há usuários")
    void listar_deveRetornarVazio_quandoNenhumCadastrado() {
        assertTrue(business.listar().isEmpty());
    }

    @Test
    @DisplayName("listarPorPerfil: deve retornar apenas clientes")
    void listarPorPerfil_deveRetornarApenasClientes() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));
        business.cadastrar(new Cliente("2", "Maria", "maria@email.com", "456"));
        business.cadastrar(new Administrador("3", "Carlos", "carlos@email.com", "abc", 1, "TI"));

        Map<String, Usuario> clientes = business.listarPorPerfil("CLIENTE");

        assertEquals(2, clientes.size());
        assertTrue(clientes.containsKey("1"));
        assertTrue(clientes.containsKey("2"));
        assertFalse(clientes.containsKey("3"));
    }

    @Test
    @DisplayName("listarPorPerfil: deve retornar apenas funcionários")
    void listarPorPerfil_deveRetornarApenasFuncionarios() {
        business.cadastrar(new Funcionario("1", "Ana", "ana@email.com", "123", "Caixa"));
        business.cadastrar(new Cliente("2", "João", "joao@email.com", "456"));

        Map<String, Usuario> funcionarios = business.listarPorPerfil("FUNCIONARIO");

        assertEquals(1, funcionarios.size());
        assertTrue(funcionarios.containsKey("1"));
    }

    @Test
    @DisplayName("listarPorPerfil: deve retornar vazio quando perfil não tem usuários")
    void listarPorPerfil_deveRetornarVazio_quandoNenhumNoPerfil() {
        business.cadastrar(new Cliente("1", "João", "joao@email.com", "123"));

        Map<String, Usuario> admins = business.listarPorPerfil("ADMINISTRADOR");

        assertTrue(admins.isEmpty());
    }
}