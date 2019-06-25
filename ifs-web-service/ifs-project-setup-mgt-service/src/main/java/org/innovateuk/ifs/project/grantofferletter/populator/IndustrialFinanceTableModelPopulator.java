package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.OtherCost;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.IndustrialFinanceTableModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.OtherCostsRowModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_COSTS;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 *  Populator for the grant offer letter industrial finance table
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
            BigDecimal totalEligibleCosts = calculateEligibleTotalFromFinances(industrialFinances.values());
            BigDecimal totalGrant = calculateTotalGrantFromFinances(industrialFinances.values());
            List<OtherCostsRowModel> otherCosts = calculateOtherCosts(industrialFinances);

            return new IndustrialFinanceTableModel(
                    industrialFinances.size() > 1,
                    industrialFinances,
                    organisations,
                    totalEligibleCosts,
                    totalGrant,
                    otherCosts
            );
        }
    }

    private List<OtherCostsRowModel> calculateOtherCosts(Map<String, ProjectFinanceResource> finances) {
        List<OtherCostsRowModel> otherCosts = new ArrayList<>();

        finances.forEach(
                (orgName, finance) ->
                        finance.getFinanceOrganisationDetails(OTHER_COSTS)
                                .getCosts()
                                .stream()
                                .map(cost -> (OtherCost)cost)
                                .forEach(cost -> {
                                    Optional<OtherCostsRowModel> existingCost = otherCosts
                                            .stream()
                                            .filter(costModel -> costModel.getDescription().equals(cost.getDescription()))
                                            .findAny();
                                    if(existingCost.isPresent()) {
                                        existingCost.get().addToCostValues(orgName, cost.getTotal());
                                    } else {
                                        otherCosts.add(new OtherCostsRowModel(cost.getDescription(),
                                                                              asMap(orgName,
                                                                                    singletonList(cost.getTotal()))
                                        ));
                                    }
                                })
        );

        return otherCosts;
    }


}
