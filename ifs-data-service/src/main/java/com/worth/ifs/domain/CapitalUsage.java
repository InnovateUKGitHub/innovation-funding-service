package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class CapitalUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    @Column(length=5000)
    private String description;
    private String newOrExisting;
    private Integer deprecation;
    private Double npv;
    private Double residualValue;
    private Integer utilisation;
    private Double netCost;

    @ManyToOne
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    public CapitalUsage(long id, String description, String newOrExisting, int deprecation, double npv, double residualValue, int utilisation, double netCost) {
        this.id = id;
        this.description = description;
        this.newOrExisting = newOrExisting;
        this.deprecation = deprecation;
        this.npv = npv;
        this.residualValue = residualValue;
        this.utilisation = utilisation;
        this.netCost = netCost;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getNewOrExisting() {
        return newOrExisting;
    }

    public Integer getDeprecation() {
        return deprecation;
    }

    public Double getNpv() {
        return npv;
    }

    public Double getResidualValue() {
        return residualValue;
    }

    public Integer getUtilisation() {
        return utilisation;
    }

    public Double getNetCost() {
        return netCost;
    }
}
