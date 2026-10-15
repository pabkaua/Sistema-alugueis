package com.loja.padraofacade;

import com.loja.business.BusinessException;
import com.loja.padraofacade.interfaces.ILojaFacade;
import com.loja.business.interfaces.*;
import com.loja.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

//Logger
import java.util.logging.Logger;


public class LojaFacade implements ILojaFacade{

    private static final ZoneId ZONA_PADRAO = ZoneId.of("America/Sao_Paulo");
    private static final Logger LOGGER = Logger.getLogger(LojaFacade.class.getName());

    private final IUsuarioBusiness usuarioBusiness;
    private final IItemBusiness itemBusiness;
    private final ICategoriaBusiness categoriaBusiness;
    private final IFornecedorBusiness fornecedorBusiness;
    private final IContratoBusiness contratoBusiness;
    private final IMultaBusiness multaBusiness;

    public LojaFacade(IUsuarioBusiness usuarioBusiness, 
                    IItemBusiness itemBusiness,
                    ICategoriaBusiness categoriaBusiness,
                    IFornecedorBusiness fornecedorBusiness,
                    IContratoBusiness contratoBusiness,
                    IMultaBusiness multaBusiness){
        this.usuarioBusiness = usuarioBusiness;
        this.itemBusiness = itemBusiness;
        this.categoriaBusiness = categoriaBusiness;
        this.fornecedorBusiness = fornecedorBusiness;
        this.contratoBusiness = contratoBusiness;
        this.multaBusiness = multaBusiness;
        resolverDependencias();
    }
    /* =========================================================================
     * 1. RELATÓRIOS
     * ========================================================================= */

    @Override
    public String gerarRelatorioItensAlugados() {
        Map<String, ContratoAluguel> ativos = contratoBusiness.listarAtivos();

        if (ativos.isEmpty()) {
            return "Nenhum item alugado no momento.";
        }

        LocalDate hoje = LocalDate.now(ZONA_PADRAO);
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== ITENS ALUGADOS: ").append(hoje).append(" ===\n\n");

        for (ContratoAluguel c : ativos.values()) {
            boolean emAtraso = hoje.isAfter(c.getDataPrevDevolucao());
            relatorio.append("Contrato : ").append(c.getId()).append("\n");
            relatorio.append("Item     : ").append(c.getItem().getNome()).append("\n");
            relatorio.append("Cliente  : ").append(c.getCliente().getNome()).append("\n");
            relatorio.append("Retirada : ").append(c.getDataRetirada()).append("\n");
            relatorio.append("Prev Dev.: ").append(c.getDataPrevDevolucao());
            if (emAtraso) relatorio.append("  *** EM ATRASO ***");
            relatorio.append("\n\n");
        }

        long atrasados = ativos.values().stream()
                .filter(c -> hoje.isAfter(c.getDataPrevDevolucao()))
                .count();

        relatorio.append("Total alugados: ").append(ativos.size())
                .append(" | Em atraso: ").append(atrasados).append("\n");

        return relatorio.toString();
    }

    @Override
    public String gerarRelatorioFaturamento(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Datas inválidas para geração de relatório de faturamento.");
        }

        BigDecimal totalAlugueis = contratoBusiness.listar().values().stream()
                .filter(
                        c -> c.getStatus().equalsIgnoreCase("ENCERRADO")
                        && c.getDataEfetivaDevolucao() != null
                        && !c.getDataEfetivaDevolucao().isBefore(inicio)
                        && !c.getDataEfetivaDevolucao().isAfter(fim))
                .map(ContratoAluguel::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMultas = multaBusiness.listar().values().stream()
                .filter(m -> m.getStatus().equalsIgnoreCase("QUITADA")
                        && m.getContrato().getDataEfetivaDevolucao() != null
                        && !m.getContrato().getDataEfetivaDevolucao().isBefore(inicio)
                        && !m.getContrato().getDataEfetivaDevolucao().isAfter(fim))
                .map(Multa::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== RELATÓRIO DE FATURAMENTO ===\n");
        relatorio.append("Período: ").append(inicio).append(" a ").append(fim).append("\n\n");
        relatorio.append(String.format("Receita com aluguéis : R$ %.2f%n", totalAlugueis));
        relatorio.append(String.format("Receita com multas   : R$ %.2f%n", totalMultas));
        relatorio.append(String.format("TOTAL                : R$ %.2f%n", totalAlugueis.add(totalMultas)));

        return relatorio.toString();
    }

    /* =========================================================================
     * 2. USUÁRIO (CLIENTE / FUNCIONÁRIO / ADM)
     * ========================================================================= */
    @Override
    public void cadastrarCliente(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("Não é possível cadastrar um cliente nulo.");
        usuarioBusiness.cadastrar(cliente);
    }

    @Override
    public void cadastrarFuncionario(Funcionario funcionario) {
        if (funcionario == null) throw new IllegalArgumentException("Não é possível cadastrar um funcionário nulo");
        usuarioBusiness.cadastrar(funcionario);
    }

    @Override
    public void cadastrarAdm(Administrador adm) {
        if (adm == null) throw new IllegalArgumentException("Não é possível cadastrar um administrador nulo");
        usuarioBusiness.cadastrar(adm);
    }

    @Override
    public Usuario buscarUsuario(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID inválido");
        return usuarioBusiness.buscarPorId(id);
    }

    @Override
    public Usuario autenticarUsuario(String email, String senha) {
        if (email == null || senha == null) throw new IllegalArgumentException("Email ou senha não podem ser nulas");
        return usuarioBusiness.autenticar(email, senha);
    }

    @Override
    public Map<String, Usuario> listarUsuario() {
        return usuarioBusiness.listar();
    }

    @Override
    public Map<String, Usuario> listarUsuarioPorPerfil(String perfil) {
        if (perfil == null || perfil.trim().isEmpty()) throw new IllegalArgumentException("Perfil inválido");
        return usuarioBusiness.listarPorPerfil(perfil);
    }

    @Override
    public void atualizarUsuario(String id, Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Não é possível atualizar um usuário nulo");
        usuarioBusiness.atualizar(usuario);
    }

    @Override
    public void desativarUsuario(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID inválido");
        Usuario usuario = usuarioBusiness.buscarPorId(id);
        usuario.setAtivo(false);
        usuarioBusiness.atualizar(usuario);
    }

    /* =========================================================================
     * 3. CONTRATO (ALUGUEL / DEVOLUÇÃO)
     * ========================================================================= */

    @Override
    public ContratoAluguel registrarAluguel(String clienteId, String itemId, LocalDate dataRetirada, LocalDate dataPrevDevolucao) {
        return contratoBusiness.registrarAluguel(clienteId, itemId, dataRetirada, dataPrevDevolucao);
    }

    @Override
    public ContratoAluguel buscarContrato(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID de contrato inválido.");
        return contratoBusiness.buscar(id);
    }

    @Override
    public ContratoAluguel processarDevolucao(String contratoId) {
        if (contratoId == null || contratoId.trim().isEmpty()) throw new IllegalArgumentException("ID inválido para processar devolução.");
        ContratoAluguel contrato = contratoBusiness.processarDevolucao(contratoId);
        if(multaBusiness.calcularAtraso(contrato).compareTo(BigDecimal.ZERO) > 0){
            multaBusiness.aplicar(contrato);
        }
        return contrato;
    }

    @Override
    public Map<String, ContratoAluguel> listarContratosAtivos() {
        return contratoBusiness.listarAtivos();
    }

    @Override
    public Map<String, ContratoAluguel> consultarHistoricoCliente(String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) throw new IllegalArgumentException("ID de cliente inválido para busca de histórico.");
        return contratoBusiness.listarPorCliente(clienteId);
    }

    /* =========================================================================
     * 4. ITEM
     * ========================================================================= */

    @Override
    public void cadastrarItem(Item i) {
        itemBusiness.cadastrar(i);
    }

    @Override
    public Item buscarItem(String id) {
        return itemBusiness.buscar(id);
    }

    @Override
    public Map<String, Item> listarItem() {
        return itemBusiness.listar();
    }

    @Override
    public Map<String, Item> listarItensDisponiveis() {
        return itemBusiness.listarPorStatus("DISPONIVEL");
    }

    @Override
    public Map<String, Item> listarItemPorCategoria(Categoria categoria) {
        return itemBusiness.listarPorCategoria(categoria);
    }

    @Override
    public Map<String, Item> listarItemPorStatus(String status) {
        return itemBusiness.listarPorStatus(status);
    }

    @Override
    public Map<String, Item> listarItemPorFornecedor(Fornecedor fornecedor) {
        return itemBusiness.listarPorFornecedor(fornecedor);
    }

    @Override
    public void atualizarItem(Item item) {
        Item existente = itemBusiness.buscar(item.getId());
        if (!existente.getStatus().equals(item.getStatus())) {
            throw new IllegalStateException("Status do item só pode ser alterado através de aluguel ou devolução.");
        }
        itemBusiness.atualizar(item);
    }

    @Override
    public void deletarItem(String id) {
        itemBusiness.deletar(id);
    }

    /* =========================================================================
     * 5. CATEGORIA
     * ========================================================================= */

    @Override
    public void cadastrarCategoria(Categoria c) {
        categoriaBusiness.cadastrar(c);
    }

    @Override
    public Categoria buscarCategoria(String id) {
        return categoriaBusiness.buscar(id);
    }

    @Override
    public void atualizarCategoria(Categoria categoria) {
        categoriaBusiness.atualizar(categoria);
    }

    @Override
    public Map<String, Categoria> listarCategoria() {
        return categoriaBusiness.listar();
    }

    @Override
    public void deletarCategoria(String id) {
        categoriaBusiness.deletar(id);
    }

    /* =========================================================================
     * 6. FORNECEDOR
     * ========================================================================= */

    @Override
    public void cadastrarFornecedor(Fornecedor f) {
        fornecedorBusiness.cadastrar(f);
    }

    @Override
    public Fornecedor buscarFornecedor(String id) {
        return fornecedorBusiness.buscar(id);
    }

    @Override
    public void atualizarFornecedor(Fornecedor fornecedor) {
        fornecedorBusiness.atualizar(fornecedor);
    }

    @Override
    public Map<String, Fornecedor> listarFornecedor() {
        return fornecedorBusiness.listar();
    }

    @Override
    public void deletarFornecedor(String id) {
        fornecedorBusiness.deletar(id);
    }

    /* =========================================================================
     * 7. MULTA
     * ========================================================================= */

    @Override
    public void aplicarMulta(ContratoAluguel contrato) {
        if (contrato == null) throw new IllegalArgumentException("Não é possível aplicar multa sobre um contrato nulo.");
        multaBusiness.aplicar(contrato);
    }

    @Override
    public void quitarMulta(String multaId) {
        if (multaId == null || multaId.trim().isEmpty()) throw new IllegalArgumentException("ID inválido para quitação de multa.");
        multaBusiness.quitar(multaId);
    }

    @Override
    public boolean possuiMultaPendente(String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) return false;
        return multaBusiness.possuiMultaPendente(clienteId);
    }

    @Override
    public Map<String, Multa> listarMultaPorCliente(String clienteId) {
        if (clienteId == null || clienteId.trim().isEmpty()) throw new IllegalArgumentException("ID de cliente inválido.");
        return multaBusiness.listarPorCliente(clienteId);
    }

    @Override
    public Map<String, Multa> listarMulta() {
        return multaBusiness.listar();
    }

    @Override
    public void deletarMulta(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID inválido para deleção de multa.");
        multaBusiness.deletarMulta(id);
    }

    /* =========================================================================
     * 8. GERAIS
     * ========================================================================= */

    public void resolverDependencias() {
        for (Item item : itemBusiness.listar().values()) {
            Categoria categoria = categoriaBusiness.buscar(item.getCategoria().getId());
            Fornecedor fornecedor = fornecedorBusiness.buscar(item.getFornecedor().getId());
            item.setCategoria(categoria);
            item.setFornecedor(fornecedor);
            itemBusiness.atualizar(item);
        }

        for (ContratoAluguel contrato : contratoBusiness.listar().values()) {
            Cliente cliente = (Cliente) usuarioBusiness.buscarPorId(contrato.getCliente().getId());
            Item item = itemBusiness.buscar(contrato.getItem().getId());
            contrato.setCliente(cliente);
            contrato.setItem(item);
            contratoBusiness.atualizar(contrato);
        }

        for (Multa multa : multaBusiness.listar().values()) {
            ContratoAluguel contrato = contratoBusiness.buscar(multa.getContrato().getId());
            multa.setContrato(contrato);
            multaBusiness.atualizar(multa);
        }
    }

    public void salvarTudo() {
        try { usuarioBusiness.salvarDados(); }
        catch (BusinessException e) { LOGGER.severe("Erro ao salvar usuários: " + e.getMessage()); }

        try { itemBusiness.salvarDados(); }
        catch (BusinessException e) { LOGGER.severe("Erro ao salvar itens: " + e.getMessage()); }

        try { categoriaBusiness.salvarDados(); }
        catch (BusinessException e) { LOGGER.severe("Erro ao salvar categorias: " + e.getMessage()); }

        try { fornecedorBusiness.salvarDados(); }
        catch (BusinessException e) { LOGGER.severe("Erro ao salvar fornecedores: " + e.getMessage()); }

        try { contratoBusiness.salvarDados(); }
        catch (BusinessException e) { LOGGER.severe("Erro ao salvar contratos: " + e.getMessage()); }

        try { multaBusiness.salvarDados(); }
        catch (BusinessException e) { LOGGER.severe("Erro ao salvar multas: " + e.getMessage()); }
    }
}
