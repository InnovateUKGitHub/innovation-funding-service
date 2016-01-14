package com.worth.ifs.finance.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;


/**
 * {@code CapitalUsage} implements {@link CostItem}
 */
public class CapitalUsage implements CostItem {
    private final Log log = LogFactory.getLog(getClass());
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
        // ( npv - residualValue ) * utilisation-percentage
        if(npv == null || residualValue == null || utilisation == null) {
            return BigDecimal.ZERO;
        }

        return npv.subtract(residualValue)
                .multiply(new BigDecimal(utilisation)
                .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_EVEN));
    }

}
