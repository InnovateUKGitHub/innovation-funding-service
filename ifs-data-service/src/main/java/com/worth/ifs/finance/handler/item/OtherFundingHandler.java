package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.resource.category.OtherFundingCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import com.worth.ifs.validator.OtherFundingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the other funding, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class OtherFundingHandler extends FinanceRowHandler {
    public static final String COST_KEY = "other-funding";

    @Autowired
    OtherFundingValidator validator;

    @Override
    public void validate(@NotNull FinanceRowItem costItem, @NotNull BindingResult bindingResult) {
        super.validate(costItem, bindingResult);
        validator.validate(costItem, bindingResult);
    }

    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        if (costItem instanceof OtherFunding) {
            cost = mapOtherFunding(costItem);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return new OtherFunding(cost.getId(), cost.getItem(), cost.getDescription(), cost.getItem(), cost.getCost());
    }

    private ApplicationFinanceRow mapOtherFunding(FinanceRowItem costItem) {
        OtherFunding otherFunding = (OtherFunding) costItem;
        String item;
        if (otherFunding.getOtherPublicFunding() != null) {
            item = otherFunding.getOtherPublicFunding();
        } else {
            item = otherFunding.getSecuredDate();
        }
        return new ApplicationFinanceRow(otherFunding.getId(), COST_KEY, item, otherFunding.getFundingSource(), 0, otherFunding.getFundingAmount(), null, null);
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost() {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(initializeOtherFunding());
        return costs;
    }

    private ApplicationFinanceRow initializeOtherFunding() {
        Long id = null;
        String otherPublicFunding = "";
        String fundingSource = OtherFundingCostCategory.OTHER_FUNDING;
        String securedDate = null;
        BigDecimal fundingAmount = new BigDecimal(0);
        OtherFunding costItem = new OtherFunding(id, otherPublicFunding, fundingSource, securedDate, fundingAmount);
        return toCost(costItem);
    }
}
