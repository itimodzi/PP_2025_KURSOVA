package com.example.sto.dao;

import com.example.sto.config.DatabaseConfig;
import com.example.sto.entity.Auto;
import com.example.sto.entity.Detail;
import com.example.sto.entity.RepairReport;
import com.example.sto.entity.Worker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;

public class RepairReportDAO {

    public void save(RepairReport repairReport) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            if (repairReport.getWorker() != null && repairReport.getWorker().getId() != null) {
                repairReport.setWorker(em.find(Worker.class, repairReport.getWorker().getId()));
            }

            if (repairReport.getAuto() != null && repairReport.getAuto().getId() != null) {
                repairReport.setAuto(em.find(Auto.class, repairReport.getAuto().getId()));
            }

            if (repairReport.getDetails() != null) {
                List<Detail> managedDetails = new ArrayList<>();
                for (Detail detail : repairReport.getDetails()) {
                    if (detail.getId() != null) {
                        Detail managedDetail = em.find(Detail.class, detail.getId());
                        if (managedDetail != null) {
                            managedDetails.add(managedDetail);
                        }
                    } else {
                        managedDetails.add(detail);
                    }
                }
                repairReport.setDetails(managedDetails);
            }

            if (repairReport.getId() == null) {
                em.persist(repairReport);
            } else {
                em.merge(repairReport);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving repair report", e);
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing RepairReport entity
     * @param repairReport the RepairReport to update
     * @throws RuntimeException if the update operation fails
     */
    public void update(RepairReport repairReport) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Find the existing entity to ensure it's managed
            RepairReport existingReport = em.find(RepairReport.class, repairReport.getId());
            if (existingReport == null) {
                throw new RuntimeException("RepairReport with ID " + repairReport.getId() + " not found");
            }

            // Update the fields that can be modified
            existingReport.setStatus(repairReport.getStatus());
            existingReport.setTotalCost(repairReport.getTotalCost());
            existingReport.setWorkedHours(repairReport.getWorkedHours());
            existingReport.setDate(repairReport.getDate());

            // Handle Worker relationship
            if (repairReport.getWorker() != null && repairReport.getWorker().getId() != null) {
                Worker managedWorker = em.find(Worker.class, repairReport.getWorker().getId());
                existingReport.setWorker(managedWorker);
            }

            // Handle Auto relationship
            if (repairReport.getAuto() != null && repairReport.getAuto().getId() != null) {
                Auto managedAuto = em.find(Auto.class, repairReport.getAuto().getId());
                existingReport.setAuto(managedAuto);
            }

            // Handle Details relationship
            if (repairReport.getDetails() != null) {
                List<Detail> managedDetails = new ArrayList<>();
                for (Detail detail : repairReport.getDetails()) {
                    if (detail.getId() != null) {
                        Detail managedDetail = em.find(Detail.class, detail.getId());
                        if (managedDetail != null) {
                            managedDetails.add(managedDetail);
                        }
                    } else {
                        managedDetails.add(detail);
                    }
                }
                existingReport.setDetails(managedDetails);
            }

            // The entity is already managed, so changes will be automatically persisted
            em.merge(existingReport);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating repair report", e);
        } finally {
            em.close();
        }
    }

    /**
     * Updates only the status of a RepairReport (optimized for status changes)
     * @param id the ID of the RepairReport to update
     * @param status the new status value
     * @throws RuntimeException if the update operation fails
     */
    public void updateStatus(Long id, String status) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            RepairReport report = em.find(RepairReport.class, id);
            if (report == null) {
                throw new RuntimeException("RepairReport with ID " + id + " not found");
            }

            report.setStatus(status);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating repair report status", e);
        } finally {
            em.close();
        }
    }

    public RepairReport findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.find(RepairReport.class, id);
        } finally {
            em.close();
        }
    }

    public List<RepairReport> findAll() {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.createQuery("SELECT r FROM RepairReport r LEFT JOIN FETCH r.worker LEFT JOIN FETCH r.auto", RepairReport.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds all RepairReports with a specific status
     * @param status the status to filter by
     * @return List of RepairReports with the specified status
     */
    public List<RepairReport> findByStatus(String status) {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM RepairReport r LEFT JOIN FETCH r.worker LEFT JOIN FETCH r.auto WHERE r.status = :status",
                            RepairReport.class)
                    .setParameter("status", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Counts RepairReports by status
     * @param status the status to count
     * @return the number of RepairReports with the specified status
     */
    public long countByStatus(String status) {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(r) FROM RepairReport r WHERE r.status = :status",
                            Long.class)
                    .setParameter("status", status)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            RepairReport report = em.find(RepairReport.class, id);
            if (report != null) {
                em.remove(report);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting repair report", e);
        } finally {
            em.close();
        }
    }
}