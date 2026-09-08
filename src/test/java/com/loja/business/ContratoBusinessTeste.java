package com.loja.business;

import com.loja.model.*;
import com.loja.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContratoBusinessTeste {

    private ContratoRepositoryFake contratoRepo;
    private ItemRepositoryFake itemRepo;
    private UsuarioRepositoryFake usuarioRepo;
    private CategoriaRepositoryFake categoriaRepo;
    private FornecedorRepositoryFake fornecedorRepo;

    private ContratoBusiness business;

    private Cliente clienteAdimplente;
    private Cliente clienteInadimplente;
    private Item itemDisponivel;
    private Item itemAlugado;

    @BeforeEach
    void setUp() {
        contratoRepo = new ContratoRepositoryFake();
        itemRepo = new ItemRepositoryFake();
        usuarioRepo = new UsuarioRepositoryFake();
        categoriaRepo = new CategoriaRepositoryFake();
        fornecedorRepo = new FornecedorRepositoryFake();

        ItemBusiness itemBusiness = new ItemBusiness(itemRepo, categoriaRepo, fornecedorRepo);
        UsuarioBusiness usuarioBusiness = new UsuarioBusiness(usuarioRepo);
        business = new ContratoBusiness(contratoRepo, itemBusiness, usuarioBusiness);

        // cliente adimplente
        clienteAdimplente = new Cliente("C1", "João", "joao@email.com", "123");
        usuarioRepo.salvar(clienteAdimplente);

        // cliente inadimplente
        clienteInadimplente = new Cliente("C2", "Maria", "maria@email.com", "456");
        clienteInadimplente.setInadimplente(true);
        usuarioRepo.salvar(clienteInadimplente);

        // item disponível
        itemDisponivel = new Item("I1", "Furadeira", new BigDecimal("50.00"), new BigDecimal("500.00"), "DISPONIVEL", null, null);
        itemRepo.salvar(itemDisponivel);

        // item já alugado
        itemAlugado = new Item("I2", "Escada", new BigDecimal("30.00"), new BigDecimal("300.00"), "ALUGADO", null, null);
        itemRepo.salvar(itemAlugado);
    }

    // registrarAluguel
    @Test
    @DisplayName("registrarAluguel: deve criar contrato quando cliente e item estão ok")
    void registrarAluguel_deveCriarContrato_quandoDadosValidos() {
        LocalDate retirada   = LocalDate.now();
        LocalDate devolucao  = retirada.plusDays(5);

        ContratoAluguel contrato = business.registrarAluguel("C1", "I1", retirada, devolucao);

        assertNotNull(contrato);
        assertEquals("ATIVO", contrato.getStatus());
        assertEquals(0, new BigDecimal("250").compareTo(contrato.getValorTotal()));
    }

    @Test
    @DisplayName("registrarAluguel: deve marcar item como ALUGADO após registrar")
    void registrarAluguel_deveMudarStatusItem_paraAlugado() {
        business.registrarAluguel("C1", "I1", LocalDate.now(), LocalDate.now().plusDays(3));

        assertEquals("ALUGADO", itemDisponivel.getStatus());
    }

    @Test
    @DisplayName("registrarAluguel: deve lançar exceção quando cliente é inadimplente (RN04)")
    void registrarAluguel_deveLancarExcecao_quandoClienteInadimplente() {
        assertThrows(RuntimeException.class, () ->
                business.registrarAluguel("C2", "I1", LocalDate.now(), LocalDate.now().plusDays(3))
        );
    }

    @Test
    @DisplayName("registrarAluguel: deve lançar exceção quando item não está disponível (RN01)")
    void registrarAluguel_deveLancarExcecao_quandoItemIndisponivel() {
        assertThrows(RuntimeException.class, () ->
                business.registrarAluguel("C1", "I2", LocalDate.now(), LocalDate.now().plusDays(3))
        );
    }

    @Test
    @DisplayName("registrarAluguel: deve lançar exceção quando cliente não existe")
    void registrarAluguel_deveLancarExcecao_quandoClienteNaoExiste() {
        assertThrows(RuntimeException.class, () ->
                business.registrarAluguel("INVALIDO", "I1", LocalDate.now(), LocalDate.now().plusDays(3))
        );
    }

    @Test
    @DisplayName("registrarAluguel: deve lançar exceção quando item não existe")
    void registrarAluguel_deveLancarExcecao_quandoItemNaoExiste() {
        assertThrows(RuntimeException.class, () ->
                business.registrarAluguel("C1", "INVALIDO", LocalDate.now(), LocalDate.now().plusDays(3))
        );
    }

    // processarDevolucao
    @Test
    @DisplayName("processarDevolucao: deve encerrar contrato e liberar item")
    void processarDevolucao_deveEncerrarContrato_e_liberarItem() {
        ContratoAluguel contrato = business.registrarAluguel("C1", "I1", LocalDate.now(), LocalDate.now().plusDays(3));

        ContratoAluguel encerrado = business.processarDevolucao(contrato.getId());

        assertEquals("ENCERRADO", encerrado.getStatus());
        assertNotNull(encerrado.getDataEfetivaDevolucao());
        assertEquals("DISPONIVEL", itemDisponivel.getStatus());
    }

    @Test
    @DisplayName("processarDevolucao: deve lançar exceção quando contrato não existe")
    void processarDevolucao_deveLancarExcecao_quandoContratoNaoExiste() {
        assertThrows(RuntimeException.class, () ->
                business.processarDevolucao("INVALIDO")
        );
    }

    @Test
    @DisplayName("processarDevolucao: deve lançar exceção quando contrato já está encerrado")
    void processarDevolucao_deveLancarExcecao_quandoContratoJaEncerrado() {
        ContratoAluguel contrato = business.registrarAluguel("C1", "I1", LocalDate.now(), LocalDate.now().plusDays(3));
        business.processarDevolucao(contrato.getId());

        assertThrows(RuntimeException.class, () ->
                business.processarDevolucao(contrato.getId())
        );
    }

    // listar
    @Test
    @DisplayName("listarAtivos: deve retornar apenas contratos com status ATIVO")
    void listarAtivos_deveRetornarApenasAtivos() {
        ContratoAluguel contrato = business.registrarAluguel("C1", "I1", LocalDate.now(), LocalDate.now().plusDays(3));
        business.processarDevolucao(contrato.getId()); // encerra o contrato

        Map<String, ContratoAluguel> ativos = business.listarAtivos();

        assertTrue(ativos.isEmpty());
    }

    @Test
    @DisplayName("listarPorCliente: deve retornar apenas contratos do cliente informado")
    void listarPorCliente_deveRetornarContratosDo_clienteCorreto() {
        business.registrarAluguel("C1", "I1", LocalDate.now(), LocalDate.now().plusDays(3));

        Map<String, ContratoAluguel> resultado = business.listarPorCliente("C1");

        assertEquals(1, resultado.size());
        resultado.values().forEach(c -> assertEquals("C1", c.getCliente().getId()));
    }

    @Test
    @DisplayName("listar: deve retornar todos os contratos")
    void listar_deveRetornarTodosOsContratos() {
        business.registrarAluguel("C1", "I1", LocalDate.now(), LocalDate.now().plusDays(3));

        assertEquals(1, business.listar().size());
    }
}