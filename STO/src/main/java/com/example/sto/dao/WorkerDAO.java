package com.example.sto.dao;


import com.example.sto.entity.Worker;
import com.example.sto.config.DatabaseConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class WorkerDAO {

    public void save(Worker worker) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            if (worker.getId() == null) {
                em.persist(worker);
            } else {
                em.merge(worker);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving worker", e);
        } finally {
            em.close();
        }
    }

    public Worker findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.find(Worker.class, id);
        } finally {
            em.close();
        }
    }

    public List<Worker> findAll() {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.createQuery("SELECT w FROM workers w", Worker.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Worker worker = em.find(Worker.class, id);
            if (worker != null) {
                em.remove(worker);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting worker", e);
        } finally {
            em.close();
        }
    }
}