package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.SummaryFinanceTableModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_FUNDING;

/**
 *  Populator for the grant offer letter summary finance table
 */
@Component
public class SummaryFinanceTableModelPopulator extends BaseGrantOfferLetterTablePopulator {

    public SummaryFinanceTableModel createTable(Map<OrganisationResource, ProjectFinanceResource> finances,
                                                CompetitionResource competition) {


        if (noIndustrialPartners(finances, competition) || noAcademicPartners(finances, competition)) {
            // to make it easier to reference if the table shouldn't show in the template
            return null;
        }

        BigDecimal totalEligibleCosts = calculateEligibleTotalFromFinances(finances.values());
        BigDecimal totalProjectGrant = calculateTotalGrantFromFinances(finances.values());
        BigDecimal totalOtherFunding = calculateTotalOtherFunding(finances.values());
        BigDecimal totalGrantPercentage = calculateTotalGrantPercentage(totalEligibleCosts, totalProjectGrant, totalOtherFunding);

        return new SummaryFinanceTableModel(totalEligibleCosts,
                                            totalProjectGrant,
                                            totalGrantPercentage,
                                            totalOtherFunding);
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

    private BigDecimal calculateTotalOtherFunding(Collection<ProjectFinanceResource> finances) {
        return finances
                .stream()
                .map(finance -> finance.getFinanceOrganisationDetails(OTHER_FUNDING))
                .map(FinanceRowCostCategory::getTotal)
                .filter(Objects::nonNull)
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalGrantPercentage(BigDecimal totalEligibleCosts,
                                                     BigDecimal totalProjectGrant,
                                                     BigDecimal totalOtherFunding) {
        return totalEligibleCosts.equals(ZERO) ?
                ZERO :
                totalOtherFunding
                        .add(totalProjectGrant)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalEligibleCosts, BigDecimal.ROUND_HALF_UP);
    }
}
