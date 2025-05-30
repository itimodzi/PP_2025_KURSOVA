package com.example.sto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RepairReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auto_id")
    private Auto auto;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "repair_report_details",
            joinColumns = @JoinColumn(name = "repair_report_id"),
            inverseJoinColumns = @JoinColumn(name = "detail_id")
    )
    private List<Detail> details;

    private BigDecimal workedHours;

    private BigDecimal totalCost;

    private String status;

    public BigDecimal calculateTotalCost() {
        BigDecimal detailsCost = details != null ?
                details.stream()
                        .map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add) :
                BigDecimal.ZERO;

        BigDecimal laborCost = worker != null && workedHours != null ?
                worker.getPaymentPerHour().multiply(workedHours) :
                BigDecimal.ZERO;

        return detailsCost.add(laborCost);
    }
}