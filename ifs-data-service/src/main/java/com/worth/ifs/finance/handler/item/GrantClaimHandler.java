package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.GrantClaim;

import java.math.BigDecimal;

public class GrantClaimHandler extends CostHandler {
    public static final String GRANT_CLAIM = "Grant Claim";

    @Override
    public CostItem toCostItem(Cost cost) {
        return null;
    }

    @Override
    public Cost toCost(CostItem costItem) {
        Cost cost = null;
        if (costItem instanceof GrantClaim) {
            GrantClaim grantClaim = (GrantClaim) costItem;
            return new Cost(grantClaim.getId(), "", GRANT_CLAIM, grantClaim.getGrantClaimPercentage(), BigDecimal.ZERO, null,null);
        }
        return cost;
    }
}
