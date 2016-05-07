package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.category.OtherFundingCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.OtherFunding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the other funding, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class OtherFundingHandler extends CostHandler {
    public static final String COST_KEY = "other-funding";

    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof OtherFunding) {
            cost = mapOtherFunding(costItem);
        }
        return cost;
    }

    @Override
    public CostItem toCostItem(Cost cost) {
        return new OtherFunding(cost.getId(), cost.getItem(), cost.getDescription(), cost.getItem(), cost.getCost());
    }

    private Cost mapOtherFunding(CostItem costItem) {
        OtherFunding otherFunding = (OtherFunding) costItem;
        String item;
        if (otherFunding.getOtherPublicFunding() != null) {
            item = otherFunding.getOtherPublicFunding();
        } else {
            item = otherFunding.getSecuredDate();
        }
        return new Cost(otherFunding.getId(), COST_KEY, item, otherFunding.getFundingSource(), 0, otherFunding.getFundingAmount(), null, null);
    }

    @Override
    public List<Cost> initializeCost() {
        ArrayList<Cost> costs = new ArrayList<>();
        costs.add(initializeOtherFunding());
        return costs;
    }

    private Cost initializeOtherFunding() {
        Long id = null;
        String otherPublicFunding = "";
        String fundingSource = OtherFundingCostCategory.OTHER_FUNDING;
        String securedDate = null;
        BigDecimal fundingAmount = new BigDecimal(0);
        OtherFunding costItem = new OtherFunding(id, otherPublicFunding, fundingSource, securedDate, fundingAmount);
        return toCost(costItem);
    }
}
