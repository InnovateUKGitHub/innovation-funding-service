package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator.*;

/**
 * Populator for the grant offer letter academic finance table
 */

@Component
public class GrantOfferLetterAcademicFinanceTablePopulator extends BaseGrantOfferLetterTablePopulator implements GrantOfferLetterFinanceTablePopulatorInterface {

    @Override
    public GrantOfferLetterAcademicFinanceTable createTable(Map<Organisation, List<Cost>> finances) {

        Map<String, List<Cost>> academicFinances =
                finances
                        .entrySet()
                        .stream()
                        .filter(e -> isAcademic(e.getKey().getOrganisationType()))
                        .collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue));

        if (academicFinances.isEmpty()) {
            // to make it easier to reference if there are no academic orgs in the pdf renderer
            return null;
        } else {

            Map<String, BigDecimal> incurredStaff =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               DIRECTLY_INCURRED_STAFF.getName(),
                                               DIRECTLY_INCURRED_STAFF.getLabel());

            Map<String, BigDecimal> incurredTravelSubsistence =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getName(),
                                               DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getLabel());

            Map<String, BigDecimal> incurredEquipment =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               DIRECTLY_INCURRED_EQUIPMENT.getName(),
                                               DIRECTLY_INCURRED_EQUIPMENT.getLabel());

            Map<String, BigDecimal> incurredOtherCosts =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               DIRECTLY_INCURRED_OTHER_COSTS.getName(),
                                               DIRECTLY_INCURRED_OTHER_COSTS.getLabel());

            Map<String, BigDecimal> allocatedInvestigators =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               DIRECTLY_ALLOCATED_INVESTIGATORS.getName(),
                                               DIRECTLY_ALLOCATED_INVESTIGATORS.getLabel());

            Map<String, BigDecimal> allocatedEstateCosts =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               DIRECTLY_ALLOCATED_ESTATES_COSTS.getName(),
                                               DIRECTLY_ALLOCATED_ESTATES_COSTS.getLabel());

            Map<String, BigDecimal> allocatedOtherCosts =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               DIRECTLY_INCURRED_OTHER_COSTS.getName(),
                                               DIRECTLY_INCURRED_OTHER_COSTS.getLabel());

            Map<String, BigDecimal> indirectCosts =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               INDIRECT_COSTS_OTHER_COSTS.getName(),
                                               INDIRECT_COSTS_OTHER_COSTS.getLabel());

            Map<String, BigDecimal> exceptionsStaff =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               INDIRECT_COSTS_STAFF.getName(),
                                               INDIRECT_COSTS_STAFF.getLabel());

            Map<String, BigDecimal> exceptionsTravelSubsistence =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getName(),
                                               INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getLabel());

            Map<String, BigDecimal> exceptionsEquipment =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               INDIRECT_COSTS_EQUIPMENT.getName(),
                                               INDIRECT_COSTS_EQUIPMENT.getLabel());

            Map<String, BigDecimal> exceptionsOtherCosts =
                    sumByFinancialTypeAndLabel(academicFinances,
                                               INDIRECT_COSTS_OTHER_COSTS.getName(),
                                               INDIRECT_COSTS_OTHER_COSTS.getLabel());

            List<String> organisations = new ArrayList<>(academicFinances.keySet());

            return new GrantOfferLetterAcademicFinanceTable(
                    incurredStaff,
                    incurredTravelSubsistence,
                    incurredEquipment,
                    incurredOtherCosts,
                    allocatedInvestigators,
                    allocatedEstateCosts,
                    allocatedOtherCosts,
                    indirectCosts,
                    exceptionsStaff,
                    exceptionsTravelSubsistence,
                    exceptionsEquipment,
                    exceptionsOtherCosts,
                    sumTotals(incurredStaff),
                    sumTotals(incurredTravelSubsistence),
                    sumTotals(incurredEquipment),
                    sumTotals(incurredOtherCosts),
                    sumTotals(allocatedInvestigators),
                    sumTotals(allocatedEstateCosts),
                    sumTotals(allocatedOtherCosts),
                    sumTotals(indirectCosts),
                    sumTotals(exceptionsStaff),
                    sumTotals(exceptionsTravelSubsistence),
                    sumTotals(exceptionsEquipment),
                    sumTotals(exceptionsOtherCosts),
                    organisations);
        }
    }
}
