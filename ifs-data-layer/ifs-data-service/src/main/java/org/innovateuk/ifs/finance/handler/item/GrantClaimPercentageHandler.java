package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.validator.GrantClaimPercentageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.groups.Default;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;

/**
 * Handles the grant claims, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class GrantClaimPercentageHandler extends FinanceRowHandler<GrantClaimPercentage> {
    public static final String GRANT_CLAIM = "Grant Claim";
    public static final String COST_KEY = "grant-claim";

    @Autowired
    private GrantClaimPercentageValidator grantClaimValidator;

    @Override
    public void validate(GrantClaimPercentage grantClaim, BindingResult bindingResult) {
        super.validate(grantClaim, bindingResult, Default.class);
        grantClaimValidator.validate(grantClaim, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toApplicationDomain(GrantClaimPercentage grantClaim) {
        return new ApplicationFinanceRow(grantClaim.getId(), COST_KEY, "", GRANT_CLAIM, null, grantClaim.getTotal(), null, grantClaim.getCostType());
    }

    @Override
    public ProjectFinanceRow toProjectDomain(GrantClaimPercentage grantClaim) {
        return new ProjectFinanceRow(grantClaim.getId(), COST_KEY, "", GRANT_CLAIM, null, grantClaim.getTotal(), null, grantClaim.getCostType());
    }

    @Override
    public FinanceRowItem toResource(FinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return FINANCE;
    }

    private FinanceRowItem buildRowItem(FinanceRow cost){
        return new GrantClaimPercentage(cost.getId(), mapCostValue(cost.getCost()), cost.getTarget().getId());
    }

    private Integer mapCostValue(BigDecimal cost) {
        return  Optional.ofNullable(cost)
                .map(BigDecimal::intValue)
                .orElse(null);
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost(ApplicationFinance applicationFinance) {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(initializeFundingLevel(applicationFinance));
        return costs;
    }

    private ApplicationFinanceRow initializeFundingLevel(ApplicationFinance applicationFinance) {
        Competition competition = applicationFinance.getApplication().getCompetition();
        GrantClaimPercentage costItem = new GrantClaimPercentage(applicationFinance.getId());
        if (competition.isFullyFunded()) {
            costItem.setPercentage(100);
        } else {
            costItem.setPercentage(null);
        }
        return toApplicationDomain(costItem);
    }
}
