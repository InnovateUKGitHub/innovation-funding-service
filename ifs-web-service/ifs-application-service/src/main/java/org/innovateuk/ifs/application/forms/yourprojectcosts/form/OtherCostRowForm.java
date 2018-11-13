package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherCost;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class OtherCostRowForm extends AbstractCostRowForm<OtherCost> {

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String description;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigDecimal estimate;

    public OtherCostRowForm() {}

    public OtherCostRowForm(OtherCost cost) {
        super(cost);
        this.description = cost.getDescription();
        this.estimate = cost.getCost();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getEstimate() {
        return estimate;
    }

    public void setEstimate(BigDecimal estimate) {
        this.estimate = estimate;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(description) && estimate == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.OTHER_COSTS;
    }

    @Override
    public OtherCost toCost() {
        return new OtherCost(getCostId(), description, estimate);
    }
}
