package com.loja.business.interfaces;

import com.loja.model.Usuario;
import java.util.Map;

public interface IUsuarioBusiness {
    void cadastrar(Usuario usuario);

    void deletar(String id);

    void atualizar(Usuario usuario);

    Map<String, Usuario> listar();

    Map<String, Usuario> listarPorPerfil(String perfil);

    Usuario buscarPorEmail(String email);

    Usuario buscarPorId(String id);

    Usuario autenticar(String email, String senha);

    public void salvarDados();
}
