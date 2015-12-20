package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.application.finance.cost.LabourCost;
import com.worth.ifs.application.finance.cost.OtherFunding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code OtherFundingCostCategory} implementation for {@link CostCategory}. Retrieving the other funding
 * for an application.
 */
public class OtherFundingCostCategory implements CostCategory {
    public static final String OTHER_FUNDING = "Other Funding";
    private OtherFunding otherFunding;

    List<CostItem> costs = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;

    @Override
    public List<CostItem> getCosts() {
        return costs;
    }

    @Override
    public BigDecimal getTotal() {
        total = costs.stream()
                .map(c -> c.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total;
    }

    public String getOtherFunding() {
        if (otherFunding!=null) {
            return otherFunding.getOtherPublicFunding();
        } else {
            return "";
        }
    }

    public OtherFunding getOtherFundingCostItem() {
        return otherFunding;
    }

    @Override
    public void addCost(CostItem costItem) {
        if(costItem != null) {
            OtherFunding otherFundingCost = (OtherFunding) costItem;
            if (otherFundingCost.getFundingSource().equals(OTHER_FUNDING)) {
                otherFunding = (OtherFunding) costItem;
            } else if (costItem != null) {
                costs.add(costItem);
            }
        }
    }
}
