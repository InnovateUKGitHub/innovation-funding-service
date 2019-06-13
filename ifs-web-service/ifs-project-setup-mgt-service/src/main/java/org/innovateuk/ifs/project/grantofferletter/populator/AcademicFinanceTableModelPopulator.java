package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.AcademicFinanceTableModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Populator for the grant offer letter academic finance table
 */

@Component
public class AcademicFinanceTableModelPopulator extends BaseGrantOfferLetterTablePopulator {

    public AcademicFinanceTableModel createTable(Map<OrganisationResource, ProjectFinanceResource> finances,
                                                 CompetitionResource competition) {


        Map<String, ProjectFinanceResource> academicFinances =
                finances
                        .entrySet()
                        .stream()
                        .filter(e -> isAcademic(e.getKey(), competition))
                        .collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue));

        if (academicFinances.isEmpty()) {
            // to make it easier to reference if the table shouldn't show in the template
            return null;
        } else {
            List<String> organisations = new ArrayList<>(academicFinances.keySet());
            BigDecimal totalEligibleCosts = calculateTotalFromFinances(academicFinances.values());
            BigDecimal totalGrant = calculateTotalGrantFromFinances(academicFinances.values());
            BigDecimal rateOfGrant = calculateRateOfGrant(totalEligibleCosts, totalGrant);

            return new AcademicFinanceTableModel(
                    academicFinances.size() > 1,
                    academicFinances,
                    organisations,
                    totalEligibleCosts,
                    totalGrant,
                    rateOfGrant);
        }
    }

}
