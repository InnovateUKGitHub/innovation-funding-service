package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost;

import java.math.BigInteger;

public class AdditionalCostAndDescription {

    private String description;
    private BigInteger cost;

    public AdditionalCostAndDescription() {}

    public AdditionalCostAndDescription(AdditionalCompanyCost cost) {
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
}
