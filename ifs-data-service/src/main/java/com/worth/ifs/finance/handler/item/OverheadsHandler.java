package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.resource.category.OverheadCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.Overhead;
import com.worth.ifs.finance.resource.cost.OverheadRateType;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the overheads, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class OverheadsHandler extends FinanceRowHandler {
    public static final String COST_KEY = "overhead";

    @Override
    public void validate(@NotNull FinanceRowItem costItem, @NotNull BindingResult bindingResult) {

        Overhead overhead = (Overhead) costItem;
        if(overhead.getRateType() != null && !OverheadRateType.NONE.equals(overhead.getRateType())){
            super.validate(costItem, bindingResult, Overhead.RateNotZero.class);
        }else{
            super.validate(costItem, bindingResult);
        }
    }

    @Override
    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        if (costItem instanceof Overhead) {
            Overhead overhead = (Overhead) costItem;
            Integer rate = overhead.getRate();
            String rateType = null;

            if (overhead.getRateType() != null) {
                rateType = overhead.getRateType().toString();
            }

            cost = new ApplicationFinanceRow(overhead.getId(), COST_KEY, rateType, "", rate, null, null, null);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        OverheadRateType type = OverheadRateType.valueOf(cost.getItem()) != null ? OverheadRateType.valueOf(cost.getItem()) : OverheadRateType.NONE;
        return new Overhead(cost.getId(), type, cost.getQuantity());
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost() {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(initializeAcceptRate());
        return costs;
    }

    private ApplicationFinanceRow initializeAcceptRate() {
        Overhead costItem = new Overhead();
        ApplicationFinanceRow cost = toCost(costItem);
        cost.setDescription(OverheadCostCategory.ACCEPT_RATE);
        return cost;
    }
}
