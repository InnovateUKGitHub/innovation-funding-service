package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.domain.Organisation;
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
    public GrantOfferLetterIndustrialFinanceTable createTable(Map<Organisation, List<ProjectFinanceRow>> finances) {

        Map<String, List<ProjectFinanceRow>> industrialFinances =
                finances
                        .entrySet()
                        .stream()
                        .filter(e -> !isAcademic(e.getKey().getOrganisationType()))
                        .collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue));

        if (industrialFinances.isEmpty()) {
            // to make it easier to reference if theree are no industrial orgs in the pdf renderer
            return null;
        } else {

            Map<String, BigDecimal> labour = sumByFinancialType(industrialFinances,
                                                                FinanceRowType.LABOUR.getType());
            Map<String, BigDecimal> materials = sumByFinancialType(industrialFinances,
                                                                   FinanceRowType.MATERIALS.getType());
            Map<String, BigDecimal> overheads = sumByFinancialType(industrialFinances,
                                                                   FinanceRowType.OVERHEADS.getType());
            Map<String, BigDecimal> capitalUsage = sumByFinancialType(industrialFinances,
                                                                      FinanceRowType.CAPITAL_USAGE.getType());
            Map<String, BigDecimal> subcontract = sumByFinancialType(industrialFinances,
                                                                     FinanceRowType.SUBCONTRACTING_COSTS.getType());
            Map<String, BigDecimal> travel = sumByFinancialType(industrialFinances,
                                                                FinanceRowType.TRAVEL.getType());
            Map<String, BigDecimal> otherCosts = sumByFinancialType(industrialFinances,
                                                                    FinanceRowType.OTHER_COSTS.getType());

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
