package org.innovateuk.ifs.finance.resource.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.PREVIOUS_FUNDING;

/**
 * {@code PreviousFundingCostCategory} implementation for {@link FinanceRowCostCategory}. Retrieving the previous funding
 * for an application.
 */
public class PreviousFundingCostCategory implements FinanceRowCostCategory {
    private PreviousFunding previousFunding;

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
        if(!previousFundingSet()) {
            total = BigDecimal.ZERO;
        } else {
            total = costs.stream()
                    .map(c -> c.getTotal())
                    .filter(c -> c != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public PreviousFunding getPreviousFunding() {
        return previousFunding;
    }

    public String hasHadPreviousFunding() {
        if (previousFunding!=null) {
            return previousFunding.getReceivedOtherFunding();
        } else {
            return "";
        }
    }

    public PreviousFunding getPreviousFundingCostItem() {
        return previousFunding;
    }

    @Override
    public void addCost(FinanceRowItem costItem) {
        if(costItem != null) {
            PreviousFunding previousFundingCost = (PreviousFunding) costItem;
            if (PREVIOUS_FUNDING.getName().equals(previousFundingCost.getFundingSource())) {
                previousFunding = (PreviousFunding) costItem;
            } else {
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

    @JsonIgnore
    public boolean previousFundingSet() {
        return "Yes".equals(hasHadPreviousFunding());
    }
}
