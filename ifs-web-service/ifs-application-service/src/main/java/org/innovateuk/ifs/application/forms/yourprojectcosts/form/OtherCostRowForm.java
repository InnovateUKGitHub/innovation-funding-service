package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.OtherCost;

import java.math.BigDecimal;

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
}
