package com.example.sto.dao;


import com.example.sto.entity.Detail;
import com.example.sto.config.DatabaseConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class DetailDAO {

    public void save(Detail detail) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            if (detail.getId() == null) {
                em.persist(detail);
            } else {
                em.merge(detail);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving detail", e);
        } finally {
            em.close();
        }
    }

    public Detail findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.find(Detail.class, id);
        } finally {
            em.close();
        }
    }

    public List<Detail> findAll() {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.createQuery("SELECT d FROM details d", Detail.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Detail detail = em.find(Detail.class, id);
            if (detail != null) {
                em.remove(detail);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting detail", e);
        } finally {
            em.close();
        }
    }
}