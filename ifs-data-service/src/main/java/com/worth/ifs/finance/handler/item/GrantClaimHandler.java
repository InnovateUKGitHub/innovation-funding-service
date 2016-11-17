package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.validator.GrantClaimValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.groups.Default;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the grant claims, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class GrantClaimHandler extends FinanceRowHandler {
    public static final String GRANT_CLAIM = "Grant Claim";
    public static final String COST_KEY = "grant-claim";

    @Autowired
    private  GrantClaimValidator grantClaimValidator;

    @Override
    public void validate(FinanceRowItem costItem, BindingResult bindingResult) {
        GrantClaim grantClaim = (GrantClaim) costItem;
        super.validate(grantClaim, bindingResult, Default.class);
        grantClaimValidator.validate(grantClaim, bindingResult);
    }


    @Override
    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        if (costItem instanceof GrantClaim) {
            GrantClaim grantClaim = (GrantClaim) costItem;
            return new ApplicationFinanceRow(grantClaim.getId(), COST_KEY, "", GRANT_CLAIM, grantClaim.getGrantClaimPercentage(), BigDecimal.ZERO, null,null);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return new GrantClaim(cost.getId(), cost.getQuantity());
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost() {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(initializeFundingLevel());
        return costs;
    }

    private ApplicationFinanceRow initializeFundingLevel() {
        GrantClaim costItem = new GrantClaim();
        costItem.setGrantClaimPercentage(null);
        return toCost(costItem);
    }
}
