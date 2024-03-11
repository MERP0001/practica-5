package org.example.Servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.Entidades.Articulo;

import java.util.Collections;
import java.util.List;

public class ArticleServices extends DataBaseServices<Articulo>{
    private static ArticleServices instance = null;

    private ArticleServices() {
        super(Articulo.class);
    }

    public static ArticleServices getInstance() {
        if (instance == null) {
            instance = new ArticleServices();
        }
        return instance;
    }

    public List<Articulo> findAllRecent() {
        EntityManager em = getEntityManager();
        TypedQuery<Articulo> query = em.createQuery("SELECT a FROM Articulo a ORDER BY a.fecha DESC", Articulo.class);
        List<Articulo> lista = query.getResultList();
        em.close();
        return lista;
    }

    public List<Articulo> findAllRecentPag(int pageNumber, int pageSize) throws PersistenceException {
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        EntityManager em = getEntityManager();
        try{
            TypedQuery<Articulo> query = em.createQuery("SELECT a FROM Articulo a ORDER BY a.fecha DESC", Articulo.class);
            //query.setFirstResult((pageNumber - 1) * pageSize);
            //query.setMaxResults(pageSize);
            var lista = query.getResultList();
            for(Articulo a : lista){
                a.getListaEtiquetas().size();
            }
            return lista;
        } finally {
            em.close();
        }
    }

    public List<Articulo> findAllByTag(String tag) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("SELECT a FROM Articulo a JOIN FETCH a.listaEtiquetas e WHERE e.etiqueta = :tag ORDER BY a.fecha DESC", Articulo.class);
        query.setParameter("tag", tag);
        List<Articulo> lista = query.getResultList();
        em.close();
        return lista;
    }

    public List<Articulo> findAllByTagPag(String tag, int pageNumber, int pageSize) throws PersistenceException {
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        EntityManager em = getEntityManager();
        try{
            TypedQuery<Articulo> query = em.createQuery("SELECT a FROM Articulo a JOIN FETCH a.listaEtiquetas e WHERE e.etiqueta = :tag ORDER BY a.fecha DESC", Articulo.class);
            query.setParameter("tag", tag);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Articulo buscar(long id) {
        EntityManager em = getEntityManager();
        try{
            TypedQuery<Articulo> query = em.createQuery("SELECT a FROM Articulo a WHERE a.id = :id", Articulo.class);
            query.setParameter("id", id);
            Articulo articulo = query.getSingleResult();
            return articulo;
        }finally {
            em.close();
        }
    }
}
