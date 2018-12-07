package org.innovateuk.ifs.application.forms.academiccosts.populator;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;


@Component
public class AcademicCostFormPopulator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private DefaultFinanceRowRestService defaultFinanceRowRestService;

    public void populate(AcademicCostForm form, long applicationId, long organisationId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        Map<String, AcademicCost> costMap = mapCostsByName(finance);

        form.setTsbReference(getCostByName(costMap, "tsb_reference", finance).getItem());

        form.setIncurredStaff(getCostByName(costMap, "incurred_staff", finance).getCost());
        form.setIncurredTravel(getCostByName(costMap, "incurred_travel_subsistence", finance).getCost());
        form.setIncurredOtherCosts(getCostByName(costMap, "incurred_other_costs", finance).getCost());

        form.setAllocatedInvestigators(getCostByName(costMap, "allocated_investigators", finance).getCost());
        form.setAllocatedEstateCosts(getCostByName(costMap, "allocated_estates_costs", finance).getCost());
        form.setAllocatedOtherCosts(getCostByName(costMap, "allocated_other_costs", finance).getCost());

        form.setIndirectCosts(getCostByName(costMap, "indirect_costs", finance).getCost());

        form.setExceptionsStaff(getCostByName(costMap, "exceptions_staff", finance).getCost());
        form.setExceptionsOtherCosts(getCostByName(costMap, "exceptions_other_costs", finance).getCost());

        form.setFilename(ofNullable(finance.getFinanceFileEntry())
                .map(fileEntryRestService::findOne)
                .flatMap(RestResult::getOptionalSuccessObject)
                .map(FileEntryResource::getName)
                .orElse(null));
    }

    private Map<String,AcademicCost> mapCostsByName(ApplicationFinanceResource finance) {
        return finance.getFinanceOrganisationDetails().values().stream()
                .map(FinanceRowCostCategory::getCosts)
                .flatMap(List::stream)
                .filter(AcademicCost.class::isInstance)
                .map(AcademicCost.class::cast)
                .collect(toMap(AcademicCost::getName, Function.identity()));
    }

    private AcademicCost getCostByName(Map<String, AcademicCost> costMap, String name, ApplicationFinanceResource finance) {
        AcademicCost cost = costMap.get(name);
        if (cost == null) {
            cost = new AcademicCost(null, name, BigDecimal.ZERO, null, costTypeFromName(name));
            defaultFinanceRowRestService.addWithResponse(finance. getId(), cost);
        }
        return cost;
    }

    private FinanceRowType costTypeFromName(String name) {
        switch (name) {
            case "tsb_reference":
                return YOUR_FINANCE;
            case "incurred_staff":
                return LABOUR;
            case "incurred_travel_subsistence":
                return TRAVEL;
            case "incurred_other_costs":
                return MATERIALS;
            case "allocated_investigators":
                return LABOUR;
            case "allocated_estates_costs":
                return OTHER_COSTS;
            case "allocated_other_costs":
                return OTHER_COSTS;
            case "indirect_costs":
                return OVERHEADS;
            case "exceptions_staff":
                return LABOUR;
            case "exceptions_other_costs":
                return OTHER_COSTS;
            default:
                throw new IFSRuntimeException("Unknown academic cost " + name);
        }
    }
}
