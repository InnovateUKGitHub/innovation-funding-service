package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.finance.resource.cost.CapitalUsage;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.validation.constraints.*;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class CapitalUsageRowForm extends AbstractCostRowForm<CapitalUsage> {

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @NotNull(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String item;

    @NotNull(message = NOT_BLANK_MESSAGE)
    private Boolean newItem;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer deprecation;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal netValue;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal residualValue;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @Min(value = 1, message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Max(value = 100, message = VALUE_MUST_BE_LOWER_MESSAGE)
    @Digits(integer = MAX_DIGITS_INT, fraction = 0, message = NO_DECIMAL_VALUES)
    private Integer utilisation;

    public CapitalUsageRowForm() {}

    public CapitalUsageRowForm(CapitalUsage cost) {
        super(cost);
        this.item = cost.getDescription();
        this.newItem = ofNullable(cost.getExisting()).map(existing -> existing.equals("New")).orElse(true);
        this.deprecation = cost.getDeprecation();
        this.netValue = cost.getNpv();
        this.residualValue = cost.getResidualValue();
        this.utilisation = cost.getUtilisation();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Boolean getNewItem() {
        return newItem;
    }

    public void setNewItem(Boolean newItem) {
        this.newItem = newItem;
    }

    public Integer getDeprecation() {
        return deprecation;
    }

    public void setDeprecation(Integer deprecation) {
        this.deprecation = deprecation;
    }

    public BigDecimal getNetValue() {
        return netValue;
    }

    public void setNetValue(BigDecimal netValue) {
        this.netValue = netValue;
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

    public BigDecimal getTotal() {
        // ( npv - residualValue ) * utilisation-percentage
        if (netValue == null || residualValue == null || utilisation == null) {
            return BigDecimal.ZERO;
        }

        return netValue.subtract(residualValue)
                .multiply(new BigDecimal(utilisation)
                        .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_EVEN));
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(item) && newItem == null && deprecation == null && netValue == null && residualValue == null && utilisation == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.CAPITAL_USAGE;
    }

    @Override
    public CapitalUsage toCost() {
        return new CapitalUsage(getCostId(), deprecation, item, Boolean.TRUE.equals(newItem) ? "New" : "Existing", netValue, residualValue, utilisation);
    }

}
