package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.OtherFunding;

public class OtherFundingHandler extends CostHandler {
    @Override
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
        String item = null;
        if(otherFunding.getOtherPublicFunding()!=null) {
            item = otherFunding.getOtherPublicFunding();
        } else {
            item = otherFunding.getSecuredDate();
        }
        return new Cost(otherFunding.getId(), item, otherFunding.getFundingSource(), 0, otherFunding.getFundingAmount(), null, null);
    }
}
