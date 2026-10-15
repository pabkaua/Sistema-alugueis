package com.loja.business;

import com.loja.business.interfaces.IUsuarioBusiness;
import com.loja.model.Usuario;
import com.loja.repositories.PersistenciaException;
import com.loja.repositories.interfaces.IUsuarioRepository;

import java.util.Map;

public class UsuarioBusiness implements IUsuarioBusiness {
    private IUsuarioRepository usuarioRepository;

    public UsuarioBusiness(IUsuarioRepository repository) {
        this.usuarioRepository = repository;
    }

    @Override
    public void cadastrar(Usuario usuario) {
        if (usuarioRepository.buscarPorEmail(usuario.getLogin()) != null) {
            throw new BusinessException("Login já cadastrado: " + usuario.getLogin());
        }
        usuarioRepository.salvar(usuario);
    }

    @Override
    public Usuario buscarPorId(String id) {
        Usuario usuario = usuarioRepository.buscar(id);
        if (usuario == null) {
            throw new BusinessException("Usuário não encontrado: " + id);
        }
        return usuario;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email);
        if (usuario == null) {
            throw new BusinessException("Usuário não encontrado: " + email);
        }
        return usuario;
    }

    @Override
    public Map<String, Usuario> listar() {
        return usuarioRepository.listar();
    }

    @Override
    public Map<String, Usuario> listarPorPerfil(String perfil) {
        // Delega ao repositório, que já filtra eficientemente
        return usuarioRepository.listar(perfil);
    }

    @Override
    public void atualizar(Usuario dados) {
        Usuario existente = usuarioRepository.buscar(dados.getId());
        if (existente == null) {
            throw new BusinessException("Usuário não encontrado");
        }
        existente.setNome(dados.getNome());
        existente.setLogin(dados.getLogin());
        existente.setSenha(dados.getSenha());
        usuarioRepository.atualizar(existente);
    }

    @Override
    public void deletar(String id) {
        if (usuarioRepository.buscar(id) == null) {
            throw new BusinessException("Usuário não encontrado: " + id);
        }
        usuarioRepository.deletar(id);
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new BusinessException("Email ou senha inválidos.");
        }
        return usuario;
    }

    public void salvarDados(){
        try {
            this.usuarioRepository.salvarDados();
        } catch (PersistenciaException e) {
            throw new BusinessException("Erro ao salvar dados" + e.getMessage());
        }
    }
}