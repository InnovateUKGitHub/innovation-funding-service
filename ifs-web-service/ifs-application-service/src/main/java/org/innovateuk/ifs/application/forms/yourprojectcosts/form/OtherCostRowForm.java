package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherCost;

import java.math.BigDecimal;

import static com.google.common.base.Strings.isNullOrEmpty;

public class OtherCostRowForm extends AbstractCostRowForm<OtherCost> {

    private String description;

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
