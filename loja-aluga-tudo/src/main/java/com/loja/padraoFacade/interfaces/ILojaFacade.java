package com.loja.padraoFacade.interfaces;

import com.loja.model.*;

import java.time.LocalDate;
import java.util.Map;

public interface ILojaFacade {
    // relatorios
    public String gerarRelatorioItensAlugados();
    public String gerarRelatorioFaturamento(LocalDate inicio, LocalDate fim);
    public void resolverDependencias();
    public void salvarTudo();

    // usuario
    public void cadastrarCliente(Cliente cliente);
    public void cadastrarFuncionario(Funcionario funcionario);
    public void cadastrarAdm(Administrador adm);
    public Usuario buscarUsuario(String id);
    public Usuario autenticarUsuario(String email, String senha);
    public Map<String, Usuario> listarUsuario();
    public Map<String, Usuario> listarUsuarioPorPerfil(String perfil);
    public void atualizarUsuario(String id, Usuario usuario);
    public void desativarUsuario(String id);

    // contrato
    public ContratoAluguel registrarAluguel(String clienteId, String itemId, LocalDate dataRetirada, LocalDate dataPrevDevolucao);
    public ContratoAluguel buscarContrato(String id);
    public ContratoAluguel processarDevolucao(String contratoId);
    public Map<String, ContratoAluguel> listarContratosAtivos();
    public Map<String, ContratoAluguel> consultarHistoricoCliente(String clienteId);

    // item
    public void cadastrarItem(Item i);
    public Item buscarItem(String id);
    public Map<String, Item> listarItem();
    public Map<String, Item> listarItensDisponiveis();
    public Map<String, Item> listarItemPorCategoria(Categoria categoria);
    public Map<String, Item> listarItemPorStatus(String status);
    public Map<String, Item> listarItemPorFornecedor(Fornecedor fornecedor);
    public void atualizarItem(Item item);
    public void deletarItem(String id);

    // categoria
    public void cadastrarCategoria(Categoria c);
    public Categoria buscarCategoria(String id);
    public void atualizarCategoria(Categoria categoria);
    public Map<String, Categoria> listarCategoria();
    public void deletarCategoria(String id);

    // fornecedor
    public void cadastrarFornecedor(Fornecedor f);
    public Fornecedor buscarFornecedor(String id);
    public void atualizarFornecedor(Fornecedor fornecedor);
    public Map<String, Fornecedor> listarFornecedor();
    public void deletarFornecedor(String id);

    // multa
    public void aplicarMulta(ContratoAluguel contrato);
    public void quitarMulta(String multaId);
    public boolean possuiMultaPendente(String clienteId);
    public Map<String, Multa> listarMultaPorCliente(String clienteId);
    public Map<String, Multa> listarMulta();
    public void deletarMulta(String id);
}
