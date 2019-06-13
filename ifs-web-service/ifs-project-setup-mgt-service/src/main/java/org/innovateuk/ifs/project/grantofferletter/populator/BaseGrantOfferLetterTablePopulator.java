package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.FinanceUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

/**
 * Base class for grant offer letter finance table populators
 **/

public class BaseGrantOfferLetterTablePopulator {

    @Autowired
    private FinanceUtil financeUtil;

    protected boolean isAcademic(OrganisationResource organisation, CompetitionResource competition) {
        return financeUtil.isUsingJesFinances(competition, organisation.getOrganisationType());
    }

    protected BigDecimal calculateTotalFromFinances(Collection<ProjectFinanceResource> finances) {
        return finances
                .stream()
                .map(BaseFinanceResource::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected BigDecimal calculateTotalGrantFromFinances(Collection<ProjectFinanceResource> finances) {
        return finances
                .stream()
                .map(BaseFinanceResource::getTotalFundingSought)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected BigDecimal calculateRateOfGrant(BigDecimal totalCosts, BigDecimal totalGrant) {
        return totalCosts.equals(BigDecimal.ZERO) ?
                BigDecimal.ZERO :
                totalGrant
                        .divide(totalCosts,2, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
    }
}
