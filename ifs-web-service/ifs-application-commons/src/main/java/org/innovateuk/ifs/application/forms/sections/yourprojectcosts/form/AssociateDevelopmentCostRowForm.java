package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.hibernate.validator.constraints.Length;
import org.innovateuk.ifs.finance.resource.cost.AssociateDevelopmentCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.*;

public class AssociateDevelopmentCostRowForm extends AbstractCostRowForm<AssociateDevelopmentCost> {

    @NotBlank(message = NOT_BLANK_MESSAGE)
    @Length(max = MAX_STRING_LENGTH, message = MAX_LENGTH_MESSAGE)
    private String role;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    private Integer duration;

    @NotNull(message = NOT_BLANK_MESSAGE)
    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
    @Digits(integer = MAX_DIGITS, fraction = 0, message = NO_DECIMAL_VALUES)
    private BigInteger cost;

    public AssociateDevelopmentCostRowForm() {}

    public AssociateDevelopmentCostRowForm(AssociateDevelopmentCost cost) {
        super(cost);
        this.role = cost.getRole();
        this.duration = cost.getDuration();
        this.cost = cost.getCost();
    }

    public AssociateDevelopmentCostRowForm(String role, Integer duration) {
        this.role = role;
        this.duration = duration;
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
        return !"Associate 1".equals(role) && cost == null;
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS;
    }

    @Override
    public AssociateDevelopmentCost toCost(Long financeId) {
        return new AssociateDevelopmentCost(financeId, getCostId(), role, duration, cost);
    }
}
