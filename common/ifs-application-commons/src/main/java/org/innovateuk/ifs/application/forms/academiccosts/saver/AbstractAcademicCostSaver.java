package org.innovateuk.ifs.application.forms.academiccosts.saver;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

public abstract class AbstractAcademicCostSaver extends AsyncAdaptor {
    protected static final Map<String, String> formFieldToCostName = asMap(
            "incurredStaff", "incurred_staff",
            "incurredTravel", "incurred_travel_subsistence",
            "incurredOtherCosts", "incurred_other_costs",
            "allocatedInvestigators", "allocated_investigators",
            "allocatedEstatesCosts", "allocated_estates_costs",
            "allocatedOtherCosts", "allocated_other_costs",
            "indirectCosts", "indirect_costs",
            "exceptionsStaff", "exceptions_staff",
            "exceptionsOtherCosts", "exceptions_other_costs"
    );

    protected abstract FinanceRowRestService financeRowRestService();

    protected ServiceResult<Void> save(AcademicCostForm form, BaseFinanceResource finance) {
        Map<String, AcademicCost> costMap = mapCostsByName(finance);

        List<CompletableFuture<ValidationMessages>> futures = new ArrayList<>();

        AcademicCost tsbReference = costMap.get("tsb_reference");
        tsbReference.setItem(form.getTsbReference());
        futures.add(asyncUpdate(tsbReference));

        AcademicCost incurredStaff = costMap.get("incurred_staff");
        incurredStaff.setCost(form.getIncurredStaff());
        futures.add(asyncUpdate(incurredStaff));

        AcademicCost incurredTravel = costMap.get("incurred_travel_subsistence");
        incurredTravel.setCost(form.getIncurredTravel());
        futures.add(asyncUpdate(incurredTravel));

        AcademicCost incurredOtherCosts = costMap.get("incurred_other_costs");
        incurredOtherCosts.setCost(form.getIncurredOtherCosts());
        futures.add(asyncUpdate(incurredOtherCosts));

        AcademicCost allocatedInvestigators = costMap.get("allocated_investigators");
        allocatedInvestigators.setCost(form.getAllocatedInvestigators());
        futures.add(asyncUpdate(allocatedInvestigators));

        AcademicCost allocatedEstatesCosts = costMap.get("allocated_estates_costs");
        allocatedEstatesCosts.setCost(form.getAllocatedEstateCosts());
        futures.add(asyncUpdate(allocatedEstatesCosts));

        AcademicCost allocatedOtherCosts = costMap.get("allocated_other_costs");
        allocatedOtherCosts.setCost(form.getAllocatedOtherCosts());
        futures.add(asyncUpdate(allocatedOtherCosts));

        AcademicCost indirectCosts = costMap.get("indirect_costs");
        indirectCosts.setCost(form.getIndirectCosts());
        futures.add(asyncUpdate(indirectCosts));

        AcademicCost exceptionsStaff = costMap.get("exceptions_staff");
        exceptionsStaff.setCost(form.getExceptionsStaff());
        futures.add(asyncUpdate(exceptionsStaff));

        AcademicCost exceptionsOtherCosts = costMap.get("exceptions_other_costs");
        exceptionsOtherCosts.setCost(form.getExceptionsOtherCosts());
        futures.add(asyncUpdate(exceptionsOtherCosts));

        ValidationMessages messages = new ValidationMessages();

        awaitAll(futures)
                .thenAccept(messages::addAll);

        if (messages.getErrors().isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(messages.getErrors());
        }
    }

    protected Map<String,AcademicCost> mapCostsByName(BaseFinanceResource finance) {
        return finance.getFinanceOrganisationDetails().values().stream()
                .map(FinanceRowCostCategory::getCosts)
                .flatMap(List::stream)
                .filter(AcademicCost.class::isInstance)
                .map(AcademicCost.class::cast)
                .collect(toMap(AcademicCost::getName, Function.identity()));
    }

    private CompletableFuture<ValidationMessages> asyncUpdate(FinanceRowItem rowItem) {
        return async(() -> financeRowRestService().update(rowItem).getSuccess());
    }
}
