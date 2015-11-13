package com.worth.ifs.application.finance.cost;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;


/**
 * {@code CapitalUsage} implements {@link CostItem}
 */
public class CapitalUsage implements CostItem {
    Long id;
    Integer deprecation;
    String description;
    String existing;
    BigDecimal npv;
    BigDecimal residualValue;
    Integer utilisation;

    public CapitalUsage() {

    }

    public CapitalUsage(Long id, Integer deprecation, String description, String existing,
                        BigDecimal npv, BigDecimal residualValue, Integer utilisation ) {
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

    public BigDecimal getNpv() {
        return npv;
    }

    public BigDecimal getResidualValue() {
        return residualValue;
    }

    public Integer getUtilisation() {
        return utilisation;
    }

    public BigDecimal getTotal() {
        return npv.subtract(residualValue.multiply(new BigDecimal(utilisation)));
    }

}
