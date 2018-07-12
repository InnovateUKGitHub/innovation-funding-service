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
import static org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator.INDIRECT_COSTS_EQUIPMENT;
import static org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator.INDIRECT_COSTS_OTHER_COSTS;

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

            Map<String, BigDecimal> incurredStaff = sumByFinancialType(academicFinances,
                                                                       DIRECTLY_INCURRED_STAFF.getName());

            Map<String, BigDecimal> incurredTravelSubsistence = sumByFinancialType(academicFinances,
                                                                                   DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getName());

            Map<String, BigDecimal> incurredEquipment = sumByFinancialType(academicFinances,
                                                                           DIRECTLY_INCURRED_EQUIPMENT.getName());

            Map<String, BigDecimal> incurredOtherCosts = sumByFinancialType(academicFinances,
                                                                            DIRECTLY_INCURRED_OTHER_COSTS.getName());

            Map<String, BigDecimal> allocatedInvestigators = sumByFinancialType(academicFinances,
                                                                                DIRECTLY_ALLOCATED_INVESTIGATORS.getName());

            Map<String, BigDecimal> allocatedEstateCosts = sumByFinancialType(academicFinances,
                                                                              DIRECTLY_ALLOCATED_ESTATES_COSTS.getName());

            Map<String, BigDecimal> allocatedOtherCosts = sumByFinancialType(academicFinances,
                                                                             DIRECTLY_INCURRED_OTHER_COSTS.getName());

            Map<String, BigDecimal> indirectCosts = sumByFinancialType(academicFinances,
                                                                       INDIRECT_COSTS_OTHER_COSTS.getName());

            Map<String, BigDecimal> exceptionsStaff = sumByFinancialType(academicFinances,
                                                                         INDIRECT_COSTS_STAFF.getName());

            Map<String, BigDecimal> exceptionsTravelSubsistence = sumByFinancialType(academicFinances,
                                                                                     INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getName());

            Map<String, BigDecimal> exceptionsEquipment = sumByFinancialType(academicFinances,
                                                                             INDIRECT_COSTS_EQUIPMENT.getName());

            Map<String, BigDecimal> exceptionsOtherCosts = sumByFinancialType(academicFinances,
                                                                              INDIRECT_COSTS_OTHER_COSTS.getName());

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
