package com.loja.repositories;

import com.loja.model.Usuario;
import jakarta.persistence.EntityManager;

public class UsuarioRepositoryDB{
  public void salvar(Usuario usuario){
    EntityManager em = JPAUtil.getEntityManager();

    try{
      em.getTransaction().begin();
      em.persist(usuario);
      em.getTransaction().commit();
    } catch(Exception e){
      if (em.getTransaction().isActive()){
        em.getTransaction().rollback();
      }
      throw e;
    } finally {
      em.close();
    }
  }
}
