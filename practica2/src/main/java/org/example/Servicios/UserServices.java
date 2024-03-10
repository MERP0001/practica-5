package org.example.Servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import org.example.Entidades.Usuario;

import java.util.List;

public class UserServices extends DataBaseServices<Usuario>{
    private static UserServices instance = null;

    private UserServices() {
        super(Usuario.class);
    }

    public static UserServices getInstance() {
        if (instance == null) {
            instance = new UserServices();
        }
        return instance;
    }

    public Usuario findUserByUsername(String username){
        Usuario user = null;
        user = this.find(username);

        return user;
    }

    public List<Usuario> findAll(int pageNumber, int pageSize) throws PersistenceException {
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        EntityManager em = getEntityManager();
        try{
            CriteriaQuery<Usuario> criteriaQuery = em.getCriteriaBuilder().createQuery(Usuario.class);
            criteriaQuery.select(criteriaQuery.from(Usuario.class));
            TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
