package org.example.Servicios;

import jakarta.persistence.EntityManager;
import org.example.Entidades.Comentario;

import java.util.List;

public class ComentarioServices extends DataBaseServices<Comentario>{
    private static ComentarioServices instance = null;

    private ComentarioServices() {
        super(Comentario.class);
    }

    public static ComentarioServices getInstance() {
        if (instance == null) {
            instance = new ComentarioServices();
        }
        return instance;
    }

    public List<Comentario> findAllByArticulo(long idArticulo) {
        EntityManager em = getEntityManager();
        List<Comentario> lista = em.createQuery("SELECT c FROM Comentario c WHERE c.articulo.id = :idArticulo", Comentario.class)
                .setParameter("idArticulo", idArticulo)
                .getResultList();
        em.close();
        return lista;
    }
}
