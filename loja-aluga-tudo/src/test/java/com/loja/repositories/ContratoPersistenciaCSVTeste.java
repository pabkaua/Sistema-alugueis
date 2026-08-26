package com.loja.repositories;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContratoPersistenciaCSVTeste {

    private Path arquivoTemp;
    private ContratoPersistenciaCSV repositorio;

    private Cliente cliente;
    private Item item;
    private LocalDate dataRetirada;
    private LocalDate dataPrevDevolucao;

    @BeforeEach
    void setUp() throws IOException {
        // cria um arquivo CSV temporário com cabeçalho para cada teste
        arquivoTemp = Files.createTempFile("contratos_teste", ".csv");
        Files.writeString(arquivoTemp, "id;clienteId;itemId;dataRetirada;dataPrevDevolucao;dataEfetivaDevolucao;valorTotal;status;historico\n");

        repositorio = new ContratoPersistenciaCSV(arquivoTemp.toString());

        // objetos reutilizáveis nos testes
        cliente = new Cliente("C1", "João", "joao@email.com", "123");
        item    = new Item("I1", "Furadeira", new BigDecimal("50.00"), new BigDecimal("500.00"), "DISPONIVEL", null, null);

        dataRetirada      = LocalDate.of(2025, 1, 10);
        dataPrevDevolucao = LocalDate.of(2025, 1, 15);
    }

    @AfterEach
    void tearDown() throws IOException {
        // remove o arquivo temporário após cada teste
        Files.deleteIfExists(arquivoTemp);
    }

    // salvar e buscar
    @Test
    @DisplayName("salvar e buscar: deve recuperar contrato salvo pelo id")
    void salvar_devePersistir_e_buscarDeveRecuperar() {
        ContratoAluguel contrato = new ContratoAluguel(
                "CT01", cliente, item,
                dataRetirada, dataPrevDevolucao,
                null, new BigDecimal("250.00"), "ATIVO"
        );

        repositorio.salvar(contrato);

        ContratoAluguel resultado = repositorio.buscar("CT01");
        assertNotNull(resultado);
        assertEquals("CT01", resultado.getId());
        assertEquals("ATIVO", resultado.getStatus());
    }

    @Test
    @DisplayName("buscar: deve retornar null quando id não existe")
    void buscar_deveRetornarNull_quandoIdNaoExiste() {
        assertNull(repositorio.buscar("INEXISTENTE"));
    }

    @Test
    @DisplayName("salvar: deve normalizar id para maiúsculo")
    void salvar_deveNormalizarId_paraMaiusculo() {
        ContratoAluguel contrato = new ContratoAluguel(
                "ct01", cliente, item,
                dataRetirada, dataPrevDevolucao,
                null, new BigDecimal("250.00"), "ATIVO"
        );

        repositorio.salvar(contrato);

        assertNotNull(repositorio.buscar("CT01"));
    }


    // listar
    @Test
    @DisplayName("listar: deve retornar todos os contratos salvos")
    void listar_deveRetornarTodosOsContratos() {
        repositorio.salvar(new ContratoAluguel("CT01", cliente, item, dataRetirada, dataPrevDevolucao, null, new BigDecimal("250.00"), "ATIVO"));
        repositorio.salvar(new ContratoAluguel("CT02", cliente, item, dataRetirada, dataPrevDevolucao, null, new BigDecimal("150.00"), "ENCERRADO"));

        assertEquals(2, repositorio.listar().size());
    }

    @Test
    @DisplayName("listar por status: deve retornar apenas contratos com status ATIVO")
    void listarPorStatus_deveRetornarApenasAtivos() {
        repositorio.salvar(new ContratoAluguel("CT01", cliente, item, dataRetirada, dataPrevDevolucao, null, new BigDecimal("250.00"), "ATIVO"));
        repositorio.salvar(new ContratoAluguel("CT02", cliente, item, dataRetirada, dataPrevDevolucao, null, new BigDecimal("150.00"), "ENCERRADO"));

        Map<String, ContratoAluguel> ativos = repositorio.listar("ATIVO");

        assertEquals(1, ativos.size());
        assertTrue(ativos.containsKey("CT01"));
    }

    @Test
    @DisplayName("listar por cliente: deve retornar apenas contratos do cliente informado")
    void listarPorCliente_deveRetornarContratosDoClienteCorreto() {
        Cliente outroCliente = new Cliente("C2", "Maria", "maria@email.com", "456");

        repositorio.salvar(new ContratoAluguel("CT01", cliente, item, dataRetirada, dataPrevDevolucao, null, new BigDecimal("250.00"), "ATIVO"));
        repositorio.salvar(new ContratoAluguel("CT02", outroCliente, item, dataRetirada, dataPrevDevolucao, null, new BigDecimal("150.00"), "ATIVO"));

        Map<String, ContratoAluguel> resultado = repositorio.listar(cliente);

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey("CT01"));
    }

    // atualizar
    @Test
    @DisplayName("atualizar: deve retornar true e atualizar contrato existente")
    void atualizar_deveRetornarTrue_quandoContratoExiste() {
        ContratoAluguel contrato = new ContratoAluguel(
                "CT01", cliente, item,
                dataRetirada, dataPrevDevolucao,
                null, new BigDecimal("250.00"), "ATIVO"
        );
        repositorio.salvar(contrato);

        contrato.setStatus("ENCERRADO");
        boolean resultado = repositorio.atualizar(contrato);

        assertTrue(resultado);
        assertEquals("ENCERRADO", repositorio.buscar("CT01").getStatus());
    }

    @Test
    @DisplayName("atualizar: deve retornar false quando contrato não existe")
    void atualizar_deveRetornarFalse_quandoContratoNaoExiste() {
        ContratoAluguel contrato = new ContratoAluguel(
                "INEXISTENTE", cliente, item,
                dataRetirada, dataPrevDevolucao,
                null, new BigDecimal("250.00"), "ATIVO"
        );

        assertFalse(repositorio.atualizar(contrato));
    }

    // deletar
        @Test
    @DisplayName("deletar: deve retornar true e remover contrato existente")
    void deletar_deveRetornarTrue_e_removerContrato() {
        repositorio.salvar(new ContratoAluguel("CT01", cliente, item, dataRetirada, dataPrevDevolucao, null, new BigDecimal("250.00"), "ATIVO"));

        boolean resultado = repositorio.deletar("CT01");

        assertTrue(resultado);
        assertNull(repositorio.buscar("CT01"));
    }

    @Test
    @DisplayName("deletar: deve retornar false quando contrato não existe")
    void deletar_deveRetornarFalse_quandoContratoNaoExiste() {
        assertFalse(repositorio.deletar("INEXISTENTE"));
    }

    // salvarDados e carregarDados
    @Test
    @DisplayName("salvarDados e carregarDados: deve persistir e recarregar contratos corretamente")
    void salvarDados_e_carregarDados_devemPersistirERelerContratos() throws IOException {
        ContratoAluguel contrato = new ContratoAluguel(
                "CT01", cliente, item,
                dataRetirada, dataPrevDevolucao,
                null, new BigDecimal("250.00"), "ATIVO"
        );
        repositorio.salvar(contrato);
        repositorio.salvarDados();

        // cria novo repositório apontando para o mesmo arquivo — simula reinício do sistema
        ContratoPersistenciaCSV novoRepositorio = new ContratoPersistenciaCSV(arquivoTemp.toString());

        ContratoAluguel recarregado = novoRepositorio.buscar("CT01");
        assertNotNull(recarregado);
        assertEquals("CT01", recarregado.getId());
        assertEquals("C1", recarregado.getCliente().getId());
        assertEquals("I1", recarregado.getItem().getId());
        assertEquals("ATIVO", recarregado.getStatus());
        assertEquals(0, new BigDecimal("250.00").compareTo(recarregado.getValorTotal()));
        assertEquals(dataRetirada, recarregado.getDataRetirada());
        assertEquals(dataPrevDevolucao, recarregado.getDataPrevDevolucao());
        assertNull(recarregado.getDataEfetivaDevolucao());
    }

    @Test
    @DisplayName("salvarDados e carregarDados: deve persistir dataEfetivaDevolucao quando não é nula")
    void salvarDados_devePeristir_dataEfetivaDevolucao_quandoPreenchida() {
        LocalDate dataEfetiva = LocalDate.of(2025, 1, 20);
        ContratoAluguel contrato = new ContratoAluguel(
                "CT01", cliente, item,
                dataRetirada, dataPrevDevolucao,
                dataEfetiva, new BigDecimal("250.00"), "ENCERRADO"
        );
        repositorio.salvar(contrato);
        repositorio.salvarDados();

        ContratoPersistenciaCSV novoRepositorio = new ContratoPersistenciaCSV(arquivoTemp.toString());

        assertEquals(dataEfetiva, novoRepositorio.buscar("CT01").getDataEfetivaDevolucao());
    }
}