package com.loja.repositories.interfaces;

import com.loja.model.Usuario;

import java.util.Map;

public interface IUsuarioRepository {

    void salvar(Usuario usuario);

    Usuario buscar(String id);

    Usuario buscarPorEmail(String email);

    Map<String, Usuario> listar();

    Map<String, Usuario> listar(String perfil);

    boolean atualizar(Usuario usuario);

    boolean deletar(String id);

    void carregarDados();

    void salvarDados();
}
