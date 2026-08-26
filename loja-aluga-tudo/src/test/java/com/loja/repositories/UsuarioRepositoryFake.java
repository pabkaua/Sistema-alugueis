package com.loja.repositories;

import com.loja.model.Usuario;
import com.loja.repositories.interfaces.IUsuarioRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioRepositoryFake implements IUsuarioRepository {
    private Map<String, Usuario> usuarios = new HashMap<>();

    @Override
    public void salvar(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
    }

    @Override
    public Usuario buscar(String id) {
        return usuarios.get(id);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarios.values().stream()
                .filter(usuario -> usuario.getLogin().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Map<String, Usuario> listar() {
        return Collections.unmodifiableMap(usuarios);
    }

    @Override
    public Map<String, Usuario> listar(String perfil) {
        return usuarios.entrySet().stream()
                .filter(usuario -> usuario.getValue().getPerfil().equalsIgnoreCase(perfil))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        if (!usuarios.containsKey(usuario.getId())) return false;
        usuarios.put(usuario.getId(), usuario);
        return true;
    }

    @Override
    public boolean deletar(String id) {
        return usuarios.remove(id) != null;
    }

    @Override public void carregarDados() {}
    @Override public void salvarDados() {}
}
