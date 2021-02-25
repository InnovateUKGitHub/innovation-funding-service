package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import javax.validation.constraints.Size;
import org.innovateuk.ifs.finance.resource.cost.AssociateSupportCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class AssociateSupportCostRowForm extends AbstractCostRowForm<AssociateSupportCost> {

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Size(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String description;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigInteger cost;

    public AssociateSupportCostRowForm() {}

    public AssociateSupportCostRowForm(AssociateSupportCost cost) {
        super(cost);
        this.description = cost.getDescription();
        this.cost = cost.getCost();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigInteger getCost() {
        return cost;
    }

    public void setCost(BigInteger cost) {
        this.cost = cost;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(description) && cost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.ASSOCIATE_SUPPORT;
    }

    @Override
    public AssociateSupportCost toCost(Long financeId) {
        return new AssociateSupportCost(financeId, getCostId(), description, cost);
    }
}
