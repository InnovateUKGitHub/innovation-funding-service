package org.innovateuk.ifs.finance.resource.cost;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;
import java.math.BigDecimal;


/**
 * {@code CapitalUsage} implements {@link FinanceRowItem}
 */
public class CapitalUsage extends AbstractFinanceRowItem {
    Long id;
    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer deprecation;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @NotNull(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String description;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @NotNull(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String existing;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal npv;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal residualValue;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Max(value = 100, message = VALUE_MUST_BE_LOWER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer utilisation;

    private CapitalUsage() {
        this(null);
    }

    public CapitalUsage(Long targetId) {
        super(targetId);
    }

    public CapitalUsage(Long id, Integer deprecation, String description, String existing,
                        BigDecimal npv, BigDecimal residualValue, Integer utilisation, Long targetId) {
        this(targetId);
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

    public void setDeprecation(Integer deprecation) {
        this.deprecation = deprecation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExisting() {
        return existing;
    }

    public void setExisting(String existing) {
        this.existing = existing;
    }

    public BigDecimal getNpv() {
        return npv;
    }

    public void setNpv(BigDecimal npv) {
        this.npv = npv;
    }

    public BigDecimal getResidualValue() {
        return residualValue;
    }

    public void setResidualValue(BigDecimal residualValue) {
        this.residualValue = residualValue;
    }

    public Integer getUtilisation() {
        return utilisation;
    }

    public void setUtilisation(Integer utilisation) {
        this.utilisation = utilisation;
    }

    @Override
    public BigDecimal getTotal() {
        // ( npv - residualValue ) * utilisation-percentage
        if (npv == null || residualValue == null || utilisation == null) {
            return BigDecimal.ZERO;
        }

        return npv.subtract(residualValue)
                .multiply(new BigDecimal(utilisation)
                        .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Override
    public String getName() {
        return getCostType().getType();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.CAPITAL_USAGE;
    }
}
