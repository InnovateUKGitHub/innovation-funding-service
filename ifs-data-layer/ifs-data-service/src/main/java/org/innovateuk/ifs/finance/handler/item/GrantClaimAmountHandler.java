package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.groups.Default;
import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.GRANT_CLAIM_AMOUNT;

/**
 * Handles the grant claims, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class GrantClaimAmountHandler extends FinanceRowHandler<GrantClaimAmount> {
    private static final String GRANT_CLAIM = "Grant Claim Amount";
    private static final String COST_KEY = "grant-claim-amount";

    @Override
    public void validate(GrantClaimAmount grantClaim, BindingResult bindingResult) {
        super.validate(grantClaim, bindingResult, Default.class);
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
    public GrantClaimAmount toResource(FinanceRow cost) {
        return new GrantClaimAmount(cost.getId(), cost.getCost(), cost.getTarget().getId());
    }

    @Override
    public Optional<FinanceRowType> getFinanceRowType() {
        return Optional.of(GRANT_CLAIM_AMOUNT);
    }

    @Override
    protected Optional<GrantClaimAmount> intialiseCost(Finance finance) {
        return Optional.of(new GrantClaimAmount(finance.getId()));
    }
}
