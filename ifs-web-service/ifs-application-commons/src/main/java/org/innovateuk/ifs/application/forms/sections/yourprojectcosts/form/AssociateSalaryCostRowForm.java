package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.innovateuk.ifs.finance.resource.cost.AssociateSalaryCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class AssociateSalaryCostRowForm extends AbstractCostRowForm<AssociateSalaryCost> {

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String role;

    private Integer duration;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigInteger cost;

    public AssociateSalaryCostRowForm() {}

    public AssociateSalaryCostRowForm(AssociateSalaryCost cost) {
        super(cost);
        this.role = cost.getRole();
        this.duration = cost.getDuration();
        this.cost = cost.getCost();
    }

    public AssociateSalaryCostRowForm(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public BigInteger getCost() {
        return cost;
    }

    public void setCost(BigInteger cost) {
        this.cost = cost;
    }

    @Override
    public boolean isBlank() {
        return isNullOrEmpty(role) && cost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.ASSOCIATE_SALARY_COSTS;
    }

    @Override
    public AssociateSalaryCost toCost(Long financeId) {
        return new AssociateSalaryCost(financeId, getCostId(), role, duration, cost);
    }
}
