package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.innovateuk.ifs.finance.validator.GrantClaimValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.GRANT_CLAIM_AMOUNT;

/**
 * Handles the grant claims, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class GrantClaimAmountHandler extends FinanceRowHandler<GrantClaimAmount> {
    public static final String GRANT_CLAIM = "Grant Claim Amount";
    public static final String COST_KEY = "grant-claim-amount";

    @Autowired
    private GrantClaimValidator grantClaimValidator;

    @Override
    public void validate(GrantClaimAmount grantClaim, BindingResult bindingResult) {
        super.validate(grantClaim, bindingResult, Default.class);
        grantClaimValidator.validate(grantClaim, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(GrantClaimAmount grantClaim) {
        return new ApplicationFinanceRow(grantClaim.getId(), COST_KEY, "", GRANT_CLAIM, null, grantClaim.getAmount(), null, grantClaim.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(GrantClaimAmount grantClaim) {
        return new ProjectFinanceRow(grantClaim.getId(), COST_KEY, "", GRANT_CLAIM, null, grantClaim.getAmount(), null, grantClaim.getCostType());
    }

    @Override
    public FinanceRowItem toResource(FinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return GRANT_CLAIM_AMOUNT;
    }

    private FinanceRowItem buildRowItem(FinanceRow cost) {
        return new GrantClaimAmount(cost.getId(), cost.getCost(), cost.getTarget().getId());
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost(ApplicationFinance applicationFinance) {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(initializeFundingLevel(applicationFinance));
        return costs;
    }

    private ApplicationFinanceRow initializeFundingLevel(ApplicationFinance applicationFinance) {
        GrantClaimAmount costItem = new GrantClaimAmount(applicationFinance.getId());
        return toApplicationDomain(costItem);
    }
}
