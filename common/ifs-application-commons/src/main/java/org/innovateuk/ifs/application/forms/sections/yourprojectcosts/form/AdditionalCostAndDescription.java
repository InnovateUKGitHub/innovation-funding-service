package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost;

import javax.validation.constraints.DecimalMin;
import java.math.BigInteger;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.VALUE_MUST_BE_HIGHER_MESSAGE;

public class AdditionalCostAndDescription {

    private String description;

    @DecimalMin(value = "1", message = VALUE_MUST_BE_HIGHER_MESSAGE)
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

    public boolean isCostIsNotNull() {
        return cost != null;
    }
    public boolean isDescriptionIsNotNull() {
        return !isNullOrEmpty(description);
    }
}
