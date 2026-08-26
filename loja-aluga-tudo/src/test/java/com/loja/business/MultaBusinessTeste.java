package com.loja.business;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Item;
import com.loja.model.Multa;
import com.loja.repositories.MultaRepositoryFake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MultaBusinessTeste {

    private MultaRepositoryFake repositorio;
    private MultaBusiness business;
    private Cliente clienteExemplo;
    private ContratoAluguel contratoComAtraso;
    private ContratoAluguel contratoEmDia;

    @BeforeEach
    void setUp() {
        repositorio = new MultaRepositoryFake();
        business = new MultaBusiness(repositorio);

        clienteExemplo = new Cliente("c1", "Diego Lyra", "diego@email.com", "123");

        Item item = new Item();
        item.setId("i1");
        item.setNome("Furadeira Elétrica");

        contratoComAtraso = new ContratoAluguel();
        contratoComAtraso.setId("ct1");
        contratoComAtraso.setCliente(clienteExemplo);
        contratoComAtraso.setItem(item);
        contratoComAtraso.setDataRetirada(LocalDate.now().minusDays(10));
        contratoComAtraso.setDataPrevDevolucao(LocalDate.now().minusDays(5));
        contratoComAtraso.setDataEfetivaDevolucao(LocalDate.now()); // 5 dias de atraso
        contratoComAtraso.setValorTotal(47.50);
        contratoComAtraso.setStatus("FINALIZADO");

        // Construindo Contrato Em Dia
        contratoEmDia = new ContratoAluguel();
        contratoEmDia.setId("ct2");
        contratoEmDia.setCliente(clienteExemplo);
        contratoEmDia.setItem(item);
        contratoEmDia.setDataRetirada(LocalDate.now().minusDays(5));
        contratoEmDia.setDataPrevDevolucao(LocalDate.now());
        contratoEmDia.setDataEfetivaDevolucao(LocalDate.now().minusDays(1)); // Entregue dentro do prazo
        contratoEmDia.setValorTotal(20.00);
        contratoEmDia.setStatus("FINALIZADO");
    }

    @Test
    @DisplayName("aplicar: deve gerar multa com ID autoincrementado e status PENDENTE")
    void aplicar_deveGerarMulta_comIdAutoincrementado() {
        business.aplicar(contratoComAtraso);

        Map<String, Multa> todas = repositorio.listar();
        assertEquals(1, todas.size());
        
        Multa gerada = todas.values().iterator().next();
        assertEquals("1", gerada.getId());
        assertEquals("PENDENTE", gerada.getStatus());
    }

    @Test
    @DisplayName("aplicar: deve lançar exceção se o contrato não contiver atraso")
    void aplicar_deveLancarExcecao_quandoContratoNaoTemAtraso() {
        assertThrows(RuntimeException.class, () -> business.aplicar(contratoEmDia));
    }

    @Test
    @DisplayName("quitar: deve alterar status para QUITADA se ID existir")
    void quitar_deveAlterarStatus_quandoIdExiste() {
        Multa multa = new Multa("10", contratoComAtraso, "Atraso", new BigDecimal("20.00"), new BigDecimal("5.50"), 5, "PENDENTE");
        repositorio.salvar(multa);

        business.quitar("10");

        Multa recuperada = repositorio.buscar("10");
        assertEquals("QUITADA", recuperada.getStatus());
    }

    @Test
    @DisplayName("quitar: deve lançar exceção ao passar ID inválido ou inexistente")
    void quitar_deveLancarExcecao_quandoIdInvalidoOuInexistente() {
        assertThrows(RuntimeException.class, () -> business.quitar("   "));
        assertThrows(RuntimeException.class, () -> business.quitar("999-inexistente"));
    }

    @Test
    @DisplayName("listarPorCliente: deve retornar apenas as multas associadas ao ID do cliente")
    void listarPorCliente_deveFiltrarCorretamente() {
        Multa m1 = new Multa("1", contratoComAtraso, "Atraso", new BigDecimal("20.00"), new BigDecimal("5.50"), 5, "PENDENTE");
        repositorio.salvar(m1);

        Map<String, Multa> resultado = business.listarPorCliente("c1");
        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey("1"));
        
        assertThrows(RuntimeException.class, () -> business.listarPorCliente(""));
    }

    @Test
    @DisplayName("possuiMultaPendente: deve analisar o status textual PENDENTE do cliente")
    void possuiMultaPendente_deveRetornarTrue_quandoHouverFaturamentoPendente() {
        Multa m1 = new Multa("1", contratoComAtraso, "Atraso", new BigDecimal("20.00"), new BigDecimal("5.50"), 5, "PENDENTE");
        repositorio.salvar(m1);

        assertTrue(business.possuiMultaPendente("c1"));

        m1.setStatus("QUITADA");
        assertFalse(business.possuiMultaPendente("c1"));
    }

    @Test
    @DisplayName("atualizar: deve disparar erro se a multa não estiver previamente no repositório")
    void atualizar_deveLancarErro_quandoMultaNaoExisteNoSistema() {
        Multa mFalsa = new Multa("999", contratoComAtraso, "Atraso", new BigDecimal("20.00"), new BigDecimal("5.50"), 5, "PENDENTE");
        
        assertThrows(RuntimeException.class, () -> business.atualizar(mFalsa));
    }

    @Test
    @DisplayName("deletarMulta: deve apagar registro e lançar erro se o repositório retornar false")
    void deletarMulta_deveRemoverRegistroOuLancarErro() {
        Multa m1 = new Multa("5", contratoComAtraso, "Atraso", new BigDecimal("20.00"), new BigDecimal("5.50"), 5, "PENDENTE");
        repositorio.salvar(m1);

        assertDoesNotThrow(() -> business.deletarMulta("5"));
        assertNull(repositorio.buscar("5"));

        assertThrows(RuntimeException.class, () -> business.deletarMulta("5"));
    }
}