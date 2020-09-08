package org.innovateuk.ifs.finance.resource.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseOtherFundingCostCategory implements FinanceRowCostCategory {
    public static final String OTHER_FUNDING = "Other Funding";
    private BaseOtherFunding otherFunding;

    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = ZERO_COST;

    @Override
    public List<FinanceRowItem> getCosts() {
        return costs;
    }

    @Override
    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public void calculateTotal() {
        if (!otherFundingSet()) {
            total = ZERO_COST;
        } else {
            total = costs.stream()
                    .map(c -> c.getTotal())
                    .filter(c -> c != null)
                    .reduce(ZERO_COST, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    public BaseOtherFunding getOtherFunding() {
        return otherFunding;
    }

    public String getOtherPublicFunding() {
        if (otherFunding != null) {
            return otherFunding.getOtherPublicFunding();
        } else {
            return "";
        }
    }

    protected abstract BaseOtherFunding getFunding();

    public BaseOtherFunding getOtherFundingCostItem() {
        return otherFunding;
    }

    @Override
    public void addCost(FinanceRowItem costItem) {
        if (costItem != null) {
            BaseOtherFunding otherFundingCost = (BaseOtherFunding) costItem;
            if (OTHER_FUNDING.equals(otherFundingCost.getFundingSource())) {
                otherFunding = (BaseOtherFunding) costItem;
            } else {
                costs.add(costItem);
            }
        }
    }

    @Override
    public boolean excludeFromTotalCost() {
        return true;
    }

    @JsonIgnore
    public boolean otherFundingSet() {
        return "Yes".equals(getOtherPublicFunding());
    }
}