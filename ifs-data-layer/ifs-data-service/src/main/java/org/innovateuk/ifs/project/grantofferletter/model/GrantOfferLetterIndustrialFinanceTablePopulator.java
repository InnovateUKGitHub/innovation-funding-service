package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
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
public class GrantOfferLetterIndustrialFinanceTablePopulator extends BaseGrantOfferLetterTablePopulator implements GrantOfferLetterFinanceTablePopulatorInterface {

    @Override
    public GrantOfferLetterIndustrialFinanceTable createTable(Map<Organisation, List<Cost>> finances) {

        Map<String, List<Cost>> industrialFinances =
                finances
                        .entrySet()
                        .stream()
                        .filter(e -> !isAcademic(e.getKey().getOrganisationType()))
                        .collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue));

        if (industrialFinances.isEmpty()) {
            // to make it easier to reference if there are no industrial orgs in the pdf renderer
            return null;
        } else {

            Map<String, BigDecimal> labour = sumByFinancialType(industrialFinances,
                                                                FinanceRowType.LABOUR.getName());

            Map<String, BigDecimal> materials = sumByFinancialType(industrialFinances,
                                                                   FinanceRowType.MATERIALS.getName());

            Map<String, BigDecimal> overheads = sumByFinancialType(industrialFinances,
                                                                   FinanceRowType.OVERHEADS.getName());

            Map<String, BigDecimal> capitalUsage = sumByFinancialType(industrialFinances,
                                                                      FinanceRowType.CAPITAL_USAGE.getName());

            Map<String, BigDecimal> subcontract = sumByFinancialType(industrialFinances,
                                                                     FinanceRowType.SUBCONTRACTING_COSTS.getName());

            Map<String, BigDecimal> travel = sumByFinancialType(industrialFinances,
                                                                FinanceRowType.TRAVEL.getName());

            Map<String, BigDecimal> otherCosts = sumByFinancialType(industrialFinances,
                                                                    FinanceRowType.OTHER_COSTS.getName());

            List<String> organisations = new ArrayList<>(industrialFinances.keySet());

            return new GrantOfferLetterIndustrialFinanceTable(
                    labour,
                    materials,
                    overheads,
                    capitalUsage,
                    subcontract,
                    travel,
                    otherCosts,
                    sumTotals(labour),
                    sumTotals(materials),
                    sumTotals(overheads),
                    sumTotals(capitalUsage),
                    sumTotals(subcontract),
                    sumTotals(travel),
                    sumTotals(otherCosts),
                    organisations);
        }
    }
}
