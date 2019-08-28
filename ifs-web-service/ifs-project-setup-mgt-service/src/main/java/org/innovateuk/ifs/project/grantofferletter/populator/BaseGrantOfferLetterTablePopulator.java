package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

/**
 * Base class for grant offer letter finance table populators
 **/
public abstract class BaseGrantOfferLetterTablePopulator {

    protected boolean isAcademic(OrganisationResource organisation, CompetitionResource competition) {
        return competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum());
    }

    protected BigDecimal calculateEligibleTotalFromFinances(Collection<ProjectFinanceResource> finances) {
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
}
