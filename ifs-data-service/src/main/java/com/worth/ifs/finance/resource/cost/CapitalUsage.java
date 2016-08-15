package com.worth.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;
import java.math.BigDecimal;


/**
 * {@code CapitalUsage} implements {@link FinanceRowItem}
 */
public class CapitalUsage implements FinanceRowItem {
    Long id;
    String name;
    @NotNull
    @Min(1)
    @Digits(integer = MAX_DIGITS_INT, fraction = MAX_FRACTION)
    Integer deprecation;

    @NotBlank
    @NotNull
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    String description;

    @NotBlank
    @NotNull
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    String existing;

    @NotNull
    @DecimalMin(value = "1")
    @Digits(integer = MAX_DIGITS, fraction = MAX_FRACTION)
    BigDecimal npv;

    @DecimalMin(value = "0")
    @Digits(integer = MAX_DIGITS, fraction = MAX_FRACTION)
    BigDecimal residualValue;

    @NotNull
    @Min(0)
    @Max(100)
    @Digits(integer = MAX_DIGITS_INT, fraction = MAX_FRACTION)
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
    public FinanceRowType getCostType() {
        return FinanceRowType.CAPITAL_USAGE;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNpv(BigDecimal npv) {
        this.npv = npv;
    }

    public void setResidualValue(BigDecimal residualValue) {
        this.residualValue = residualValue;
    }

    public void setUtilisation(Integer utilisation) {
        this.utilisation = utilisation;
    }

    public void setDeprecation(Integer deprecation) {
        this.deprecation = deprecation;
    }

    public void setExisting(String existing) {
        this.existing = existing;
    }
}
