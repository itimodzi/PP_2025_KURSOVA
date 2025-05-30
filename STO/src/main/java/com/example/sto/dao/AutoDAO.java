package com.example.sto.dao;

import com.example.sto.entity.Auto;
import com.example.sto.config.DatabaseConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;


public class AutoDAO {

    public void save(Auto auto) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            if (auto.getId() == null) {
                em.persist(auto);
            } else {
                em.merge(auto);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving auto", e);
        } finally {
            em.close();
        }
    }

    public Auto findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.find(Auto.class, id);
        } finally {
            em.close();
        }
    }

    public List<Auto> findAll() {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM Auto a", Auto.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Auto auto = em.find(Auto.class, id);
            if (auto != null) {
                em.remove(auto);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting auto", e);
        } finally {
            em.close();
        }
    }
}