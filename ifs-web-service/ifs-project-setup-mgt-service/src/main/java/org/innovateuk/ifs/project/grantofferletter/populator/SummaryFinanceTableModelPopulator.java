package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.SummaryFinanceTableModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Populator for the grant offer letter summary finance table
 */

@Component
public class SummaryFinanceTableModelPopulator extends BaseGrantOfferLetterTablePopulator {

    public SummaryFinanceTableModel createTable(Map<OrganisationResource, ProjectFinanceResource> finances,
                                                CompetitionResource competition) {


        if (noIndustrialPartners(finances, competition) || noAcademicPartners(finances, competition)) {
            // to make it easier to reference if the table shouldn't show in the template
            return null;
        }

        BigDecimal totalProjectCosts = finances
                .values()
                .stream()
                .map(BaseFinanceResource::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProjectGrant = finances
                .values()
                .stream()
                .map(BaseFinanceResource::getTotalFundingSought)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rateOfGrant = totalProjectCosts.equals(BigDecimal.ZERO) ?
                BigDecimal.ZERO :
                totalProjectGrant
                        .divide(totalProjectCosts,2, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

        return new SummaryFinanceTableModel(totalProjectCosts,
                                            totalProjectGrant,
                                            rateOfGrant);
    }

    private boolean noIndustrialPartners(Map<OrganisationResource, ProjectFinanceResource> finances,
                                         CompetitionResource competition) {
        return finances
                .entrySet()
                .stream()
                .allMatch(e -> isAcademic(e.getKey(), competition));
    }

    private boolean noAcademicPartners(Map<OrganisationResource, ProjectFinanceResource> finances,
                                         CompetitionResource competition) {
        return finances
                .entrySet()
                .stream()
                .noneMatch(e -> isAcademic(e.getKey(), competition));
    }
}
