module com.example.sto {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.naming;

    opens com.example.sto to javafx.fxml;
    opens com.example.sto.controller to javafx.fxml;
    opens com.example.sto.entity to org.hibernate.orm.core, jakarta.persistence;

    exports com.example.sto;
    exports com.example.sto.entity;
    exports com.example.sto.dao;
    exports com.example.sto.config;
    exports com.example.sto.controller;

}
