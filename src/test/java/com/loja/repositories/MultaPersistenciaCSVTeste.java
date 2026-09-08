package com.loja.repositories;

import com.loja.model.Cliente;
import com.loja.model.ContratoAluguel;
import com.loja.model.Multa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MultaPersistenciaCSVTeste {

    private MultaPersistenciaCSV persistencia;
    private Path arquivoTemp;
    private ContratoAluguel contratoMock;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        arquivoTemp = tempDir.resolve("multas_teste.csv");
        persistencia = new MultaPersistenciaCSV(arquivoTemp.toString());

        Cliente cliente = new Cliente("c1", "Diego Lyra", "diego@email.com", "123");
        contratoMock = new ContratoAluguel();
        contratoMock.setId("ct1");
        contratoMock.setCliente(cliente);
    }

    @Test
    @DisplayName("Salvar: Deve gravar o registro fisicamente no arquivo CSV")
    void salvar_DeveGravarNoArquivoComSucesso() throws IOException {
        Multa multa = new Multa("1", contratoMock, "Atraso na devolução", new BigDecimal("15.00"), new BigDecimal("0.00"), 3, "PENDENTE");
        
        persistencia.salvar(multa);

        assertNotNull(persistencia.buscar("1"));

        var linhas = Files.readAllLines(arquivoTemp);
        assertTrue(linhas.size() > 0, "O arquivo não deveria estar vazio...");
    }

    @Test
    @DisplayName("Buscar: Deve retornar null se o ID não existir")
    void buscar_DeveRetornarNullSeIdInexistente() {
        Multa multa = persistencia.buscar("9999");
        assertNull(multa, "Deveria retornar null para um ID inexistente.");
    }

    @Test
    @DisplayName("Listar por Status: Deve filtrar ignorando maiúsculas/minúsculas")
    void listar_DeveFiltrarPorStatusIgnoreCase() {
        Multa m1 = new Multa("10", contratoMock, "Atraso", new BigDecimal("10.00"), new BigDecimal("0.00"), 2, "PENDENTE");
        Multa m2 = new Multa("11", contratoMock, "Dano ao item", new BigDecimal("50.00"), new BigDecimal("0.00"), 0, "QUITADA");
        
        persistencia.salvar(m1);
        persistencia.salvar(m2);

        Map<String, Multa> resultado = persistencia.listar("peNdEnTe");
        
        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey("10"));
        assertFalse(resultado.containsKey("11"));
    }

    @Test
    @DisplayName("Atualizar: Deve alterar o status de um registro existente")
    void atualizar_DeveModificarRegistroComSucesso() {
        Multa multa = new Multa("20", contratoMock, "Atraso", new BigDecimal("20.00"), new BigDecimal("0.00"), 4, "PENDENTE");
        persistencia.salvar(multa);

        multa.setStatus("QUITADA");
        boolean atualizou = persistencia.atualizar(multa);

        assertTrue(atualizou);
        assertEquals("QUITADA", persistencia.buscar("20").getStatus());
    }

    @Test
    @DisplayName("Deletar: Deve remover a multa da memória e do arquivo")
    void deletar_DeveRemoverRegistroComSucesso() {
        Multa multa = new Multa("30", contratoMock, "Item Danificado", new BigDecimal("100.00"), new BigDecimal("0.00"), 0, "PENDENTE");
        persistencia.salvar(multa);

        boolean deletou = persistencia.deletar("30");

        assertTrue(deletou);
        assertNull(persistencia.buscar("30"), "A multa deveria ter sido removida.");
    }
}