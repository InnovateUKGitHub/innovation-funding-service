package com.worth.ifs.application.finance.cost;

import javax.servlet.http.HttpServletRequest;

public class CapitalUsage implements CostItem {
    Long id;
    Integer deprecation;
    String description;
    String existing;
    Double npv;
    Double residualValue;
    Integer utilisation;

    public CapitalUsage() {

    }

    public CapitalUsage(Long id, Integer deprecation, String description, String existing,
        Double npv, Double residualValue, Integer utilisation ) {
        this.id = id;
        this.deprecation = deprecation;
        this.description = description;
        this.existing = existing;
        this.npv = npv;
        this.residualValue = residualValue;
        this.utilisation = utilisation;
    }

    public Long getId() {
        return id;
    }

    public Integer getDeprecation() {
        return deprecation;
    }

    public String getDescription() {
        return description;
    }

    public String getExisting() {
        return existing;
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

    public Double getTotal() {
        return npv - (residualValue * utilisation);
    }

}
