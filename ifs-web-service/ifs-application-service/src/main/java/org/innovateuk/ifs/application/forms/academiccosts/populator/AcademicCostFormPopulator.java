package org.innovateuk.ifs.application.forms.academiccosts.populator;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;


@Component
public class AcademicCostFormPopulator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    public void populate(AcademicCostForm form, long applicationId, long organisationId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        Map<String, AcademicCost> costMap = finance.getFinanceOrganisationDetails().values().stream()
                .map(FinanceRowCostCategory::getCosts)
                .flatMap(List::stream)
                .filter(AcademicCost.class::isInstance)
                .map(AcademicCost.class::cast)
                .collect(toMap(AcademicCost::getName, Function.identity()));

        form.setTsbReference(getCostByName(costMap, "tsb_reference").map(AcademicCost::getItem).orElse(null));

        form.setIncurredStaff(getCostByName(costMap, "incurred_staff").map(AcademicCost::getCost).orElse(null));
        form.setIncurredTravel(getCostByName(costMap, "incurred_travel_subsistence").map(AcademicCost::getCost).orElse(null));
        form.setIncurredOtherCosts(getCostByName(costMap, "incurred_other_costs").map(AcademicCost::getCost).orElse(null));

        form.setAllocatedInvestigators(getCostByName(costMap, "allocated_investigators").map(AcademicCost::getCost).orElse(null));
        form.setAllocatedEstateCosts(getCostByName(costMap, "allocated_estates_costs").map(AcademicCost::getCost).orElse(null));
        form.setAllocatedOtherCosts(getCostByName(costMap, "allocated_other_costs").map(AcademicCost::getCost).orElse(null));

        form.setIndirectCosts(getCostByName(costMap, "indirect_costs").map(AcademicCost::getCost).orElse(null));

        form.setExceptionsStaff(getCostByName(costMap, "exceptions_staff").map(AcademicCost::getCost).orElse(null));
        form.setExceptionsOtherCosts(getCostByName(costMap, "exceptions_other_costs").map(AcademicCost::getCost).orElse(null));

    }

    private Optional<AcademicCost> getCostByName(Map<String, AcademicCost> costMap, String name) {
        return Optional.ofNullable(costMap.get(name));
    }
    /*
    'tsb_reference','Provide the project costs for \'{organisationName}\'','YOUR_FINANCE'
'incurred_staff','Labour','LABOUR'
'incurred_travel_subsistence','Travel and subsistence','TRAVEL'
'incurred_other_costs','Materials','MATERIALS'
'allocated_investigators','Labour','LABOUR'
'allocated_estates_costs','Other costs','OTHER_COSTS'
'allocated_other_costs','Other costs','OTHER_COSTS'
'indirect_costs','Overheads','OVERHEADS'
'exceptions_staff','Labour','LABOUR'
'exceptions_other_costs','Other costs','OTHER_COSTS'
'other-funding','Other funding','OTHER_FUNDING'
'grant-claim','Funding level','FINANCE'

     */
}
