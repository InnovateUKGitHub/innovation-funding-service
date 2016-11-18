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
    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = MAX_FRACTION, message = MAX_DIGITS_MESSAGE)
    Integer deprecation;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @NotNull(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    String description;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @NotNull(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    String existing;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = MAX_FRACTION, message = MAX_DIGITS_MESSAGE)
    BigDecimal npv;

    @DecimalMin(value = "0", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = MAX_FRACTION, message = MAX_DIGITS_MESSAGE)
    BigDecimal residualValue;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 0, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Max(value = 100, message = VALUE_MUST_BE_LOWER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = MAX_FRACTION, message = MAX_DIGITS_MESSAGE)
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
