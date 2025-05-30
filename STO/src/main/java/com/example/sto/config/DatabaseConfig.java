package com.example.sto.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig {
    private static EntityManagerFactory entityManagerFactory;

    static {
        try {
            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
            properties.put("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/sto_database");
            properties.put("jakarta.persistence.jdbc.user", "postgres");
            properties.put("jakarta.persistence.jdbc.password", "03112006");
            properties.put("jakarta.persistence.schema-generation.database.action", "create");
            properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.put("hibernate.hbm2ddl.auto", "update");

            entityManagerFactory = Persistence.createEntityManagerFactory("sto-persistence-unit", properties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize EntityManagerFactory", e);
        }
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
