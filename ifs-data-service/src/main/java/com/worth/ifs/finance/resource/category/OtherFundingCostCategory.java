package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.OtherFunding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code OtherFundingCostCategory} implementation for {@link FinanceRowCostCategory}. Retrieving the other funding
 * for an application.
 */
public class OtherFundingCostCategory implements FinanceRowCostCategory {
    public static final String OTHER_FUNDING = "Other Funding";
    private OtherFunding otherFunding;

    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

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
        if(getOtherPublicFunding() == null || !"Yes".equals(getOtherPublicFunding())) {
            total = BigDecimal.ZERO;
        } else {
            total = costs.stream()

                    .map(c -> c.getTotal())
                    .filter(c -> c != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public OtherFunding getOtherFunding() {
        return otherFunding;
    }

    public String getOtherPublicFunding() {
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
    public void addCost(FinanceRowItem costItem) {
        if(costItem != null) {
            OtherFunding otherFundingCost = (OtherFunding) costItem;
            if (OTHER_FUNDING.equals(otherFundingCost.getFundingSource())) {
                otherFunding = (OtherFunding) costItem;
            } else if (costItem != null) {
                costs.add(costItem);
            }
        }
    }

    @Override
    public boolean excludeFromTotalCost() {
        return true;
    }

    @Override
    public void setCosts(List<FinanceRowItem> costItems) {
        costs = costItems;
    }
}
