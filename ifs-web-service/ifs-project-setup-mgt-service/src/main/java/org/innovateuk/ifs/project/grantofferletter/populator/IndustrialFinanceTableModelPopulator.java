package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.IndustrialFinanceTableModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Populator for the grant offer letter industrial finance table
 */

@Component
public class IndustrialFinanceTableModelPopulator extends BaseGrantOfferLetterTablePopulator {

    public IndustrialFinanceTableModel createTable(Map<OrganisationResource, ProjectFinanceResource> finances,
                                                   CompetitionResource competition) {

        Map<String, ProjectFinanceResource> industrialFinances =
                finances
                        .entrySet()
                        .stream()
                        .filter(e -> !isAcademic(e.getKey(), competition))
                        .collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue));

        if (industrialFinances.isEmpty()) {
            // to make it easier to reference if the table shouldn't show in the template
            return null;
        } else {
            List<String> organisations = new ArrayList<>(industrialFinances.keySet());
            BigDecimal totalEligibleCosts = calculateTotalFromFinances(industrialFinances.values());
            BigDecimal totalGrant = calculateTotalGrantFromFinances(industrialFinances.values());
            BigDecimal rateOfGrant = calculateRateOfGrant(totalEligibleCosts, totalGrant);

            return new IndustrialFinanceTableModel(industrialFinances.size() > 1,
                                                   industrialFinances,
                                                   organisations,
                                                   totalEligibleCosts,
                                                   totalGrant,
                                                   rateOfGrant);
        }
    }
}
