package com.worth.ifs.finance.resource.cost;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;


/**
 * {@code CapitalUsage} implements {@link CostItem}
 */
public class CapitalUsage implements CostItem {
    Long id;
    String name;
    @Min(0)
    @Digits(integer = MAX_DIGITS, fraction = 0)
    Integer deprecation;
    String description;
    String existing;
    @DecimalMin(value = "0")
    @Digits(integer = MAX_DIGITS, fraction = 0)
    BigDecimal npv;
    @DecimalMin(value = "0")
    @Digits(integer = MAX_DIGITS, fraction = 0)
    BigDecimal residualValue;
    @Min(0)
    @Max(100)
    @Digits(integer = MAX_DIGITS, fraction = 0)
    Integer utilisation;

    public CapitalUsage() {
        this.name = getCostType().getType();
    }

    public CapitalUsage(Long id, Integer deprecation, String description, String existing,
                        BigDecimal npv, BigDecimal residualValue, Integer utilisation ) {
        this();
        this.id = id;
        this.deprecation = deprecation;
        this.description = description;
        this.existing = existing;
        this.npv = npv;
        this.residualValue = residualValue;
        this.utilisation = utilisation;
    }

    @Override
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

    @Override
    public BigDecimal getTotal() {
        // ( npv - residualValue ) * utilisation-percentage
        if(npv == null || residualValue == null || utilisation == null) {
            return BigDecimal.ZERO;
        }

        return npv.subtract(residualValue)
                .multiply(new BigDecimal(utilisation)
                .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getMinRows() {
        return 0;
    }

    @Override
    public CostType getCostType() {
        return CostType.CAPITAL_USAGE;
    }
}
