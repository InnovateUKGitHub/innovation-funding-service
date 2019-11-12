package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.validator.GrantClaimPercentageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.groups.Default;
import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;

/**
 * Handles the grant claims, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class GrantClaimPercentageHandler extends FinanceRowHandler<GrantClaimPercentage> {
    private static final String GRANT_CLAIM = "Grant Claim";
    private static final String COST_KEY = "grant-claim";

    @Autowired
    private GrantClaimPercentageValidator grantClaimValidator;

    @Override
    public void validate(GrantClaimPercentage grantClaim, BindingResult bindingResult) {
        super.validate(grantClaim, bindingResult, Default.class);
        grantClaimValidator.validate(grantClaim, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(GrantClaimPercentage grantClaim) {
        return new ApplicationFinanceRow(grantClaim.getId(), COST_KEY, "", GRANT_CLAIM, grantClaim.getPercentage(), grantClaim.getTotal(), null, grantClaim.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(GrantClaimPercentage grantClaim) {
        return new ProjectFinanceRow(grantClaim.getId(), COST_KEY, "", GRANT_CLAIM, grantClaim.getPercentage(), grantClaim.getTotal(), null, grantClaim.getCostType());
    }

    @Override
    public GrantClaimPercentage toResource(FinanceRow cost) {
        return new GrantClaimPercentage(cost.getId(), cost.getQuantity(), cost.getTarget().getId());
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return FINANCE;
    }

    @Override
    protected Optional<GrantClaimPercentage> intialiseCost(Finance finance) {
        Competition competition = finance.getCompetition();
        GrantClaimPercentage costItem = new GrantClaimPercentage(finance.getId());
        if (competition.isFullyFunded()) {
            costItem.setPercentage(100);
        } else {
            costItem.setPercentage(null);
        }
        return Optional.of(costItem);
    }
}
