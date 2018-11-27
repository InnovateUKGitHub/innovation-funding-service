package org.innovateuk.ifs.application.forms.academiccosts.saver;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Component
public class AcademicCostSaver {
    private final static Logger LOG = LoggerFactory.getLogger(AcademicCostSaver.class);

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private DefaultFinanceRowRestService financeRowRestService;

    public ServiceResult<Void> save(AcademicCostForm form, long applicationId, long organisationId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        Map<String, AcademicCost> costMap = finance.getFinanceOrganisationDetails().values().stream()
                .map(FinanceRowCostCategory::getCosts)
                .flatMap(List::stream)
                .filter(AcademicCost.class::isInstance)
                .map(AcademicCost.class::cast)
                .collect(toMap(AcademicCost::getName, Function.identity()));

        ValidationMessages messages = new ValidationMessages();

        AcademicCost tsbReference = costMap.get("tsb_reference");
        tsbReference.setItem(form.getTsbReference());
        messages.addAll(financeRowRestService.update(tsbReference).getSuccess());

        AcademicCost incurredStaff = costMap.get("incurred_staff");
        incurredStaff.setCost(form.getIncurredStaff());
        messages.addAll(financeRowRestService.update(incurredStaff).getSuccess());

        AcademicCost incurredTravel = costMap.get("incurred_travel_subsistence");
        incurredTravel.setCost(form.getIncurredTravel());
        messages.addAll(financeRowRestService.update(incurredTravel).getSuccess());

        AcademicCost incurredOtherCosts = costMap.get("incurred_other_costs");
        incurredOtherCosts.setCost(form.getIncurredOtherCosts());
        messages.addAll(financeRowRestService.update(incurredOtherCosts).getSuccess());

        AcademicCost allocatedInvestigators = costMap.get("allocated_investigators");
        allocatedInvestigators.setCost(form.getAllocatedInvestigators());
        messages.addAll(financeRowRestService.update(allocatedInvestigators).getSuccess());

        AcademicCost allocatedEstatesCosts = costMap.get("allocated_estates_costs");
        allocatedEstatesCosts.setCost(form.getAllocatedEstateCosts());
        messages.addAll(financeRowRestService.update(allocatedEstatesCosts).getSuccess());

        AcademicCost allocatedOtherCosts = costMap.get("allocated_other_costs");
        allocatedOtherCosts.setCost(form.getAllocatedOtherCosts());
        messages.addAll(financeRowRestService.update(allocatedOtherCosts).getSuccess());

        AcademicCost indirectCosts = costMap.get("indirect_costs");
        indirectCosts.setCost(form.getIndirectCosts());
        messages.addAll(financeRowRestService.update(indirectCosts).getSuccess());

        AcademicCost exceptionsStaff = costMap.get("exceptions_staff");
        exceptionsStaff.setCost(form.getExceptionsStaff());
        messages.addAll(financeRowRestService.update(exceptionsStaff).getSuccess());

        AcademicCost exceptionsOtherCosts = costMap.get("exceptions_other_costs");
        exceptionsOtherCosts.setCost(form.getExceptionsOtherCosts());
        messages.addAll(financeRowRestService.update(exceptionsOtherCosts).getSuccess());

        if (messages.getErrors().isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(messages.getErrors());
        }
    }

    public Optional<Long> autoSave(String field, String value, long applicationId, long organisationId) {
        try {
            ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
            Map<String, AcademicCost> costMap = finance.getFinanceOrganisationDetails().values().stream()
                    .map(FinanceRowCostCategory::getCosts)
                    .flatMap(List::stream)
                    .filter(AcademicCost.class::isInstance)
                    .map(AcademicCost.class::cast)
                    .collect(toMap(AcademicCost::getName, Function.identity()));


            switch (field) {
                case "tsbReference":
                    AcademicCost tsbReference = costMap.get("tsb_reference");
                    tsbReference.setItem(value);
                    financeRowRestService.update(tsbReference).getSuccess();
                    break;
                case "incurredStaff":
                    AcademicCost incurredStaff = costMap.get("incurred_staff");
                    incurredStaff.setCost(new BigDecimal(value));
                    financeRowRestService.update(incurredStaff).getSuccess();
                    break;
                case "incurredTravel":
                    AcademicCost incurredTravel = costMap.get("incurred_travel_subsistence");
                    incurredTravel.setCost(new BigDecimal(value));
                    financeRowRestService.update(incurredTravel).getSuccess();
                    break;
                case "incurredOtherCosts":
                    AcademicCost incurredOtherCosts = costMap.get("incurred_other_costs");
                    incurredOtherCosts.setCost(new BigDecimal(value));
                    financeRowRestService.update(incurredOtherCosts).getSuccess();
                    break;
                case "allocatedInvestigators":
                    AcademicCost allocatedInvestigators = costMap.get("allocated_investigators");
                    allocatedInvestigators.setCost(new BigDecimal(value));
                    financeRowRestService.update(allocatedInvestigators).getSuccess();
                    break;
                case "allocatedEstatesCosts":
                    AcademicCost allocatedEstatesCosts = costMap.get("allocated_estates_costs");
                    allocatedEstatesCosts.setCost(new BigDecimal(value));
                    financeRowRestService.update(allocatedEstatesCosts).getSuccess();
                    break;
                case "allocatedOtherCosts":
                    AcademicCost allocatedOtherCosts = costMap.get("allocated_other_costs");
                    allocatedOtherCosts.setCost(new BigDecimal(value));
                    financeRowRestService.update(allocatedOtherCosts).getSuccess();
                    break;
                case "indirectCosts":
                    AcademicCost indirectCosts = costMap.get("indirect_costs");
                    indirectCosts.setCost(new BigDecimal(value));
                    financeRowRestService.update(indirectCosts).getSuccess();
                    break;
                case "exceptionsStaff":
                    AcademicCost exceptionsStaff = costMap.get("exceptions_staff");
                    exceptionsStaff.setCost(new BigDecimal(value));
                    financeRowRestService.update(exceptionsStaff).getSuccess();
                    break;
                case "exceptionsOtherCosts":
                    AcademicCost exceptionsOtherCosts = costMap.get("exceptions_other_costs");
                    exceptionsOtherCosts.setCost(new BigDecimal(value));
                    financeRowRestService.update(exceptionsOtherCosts).getSuccess();
                    break;
                default:
                    throw new IFSRuntimeException(String.format("Auto save field not handled %s", field), Collections.emptyList());
            }
        } catch (Exception e) {
            LOG.debug("Error auto saving", e);
            LOG.info(String.format("Unable to auto save field (%s) value (%s)", field, value));
        }

        return Optional.empty();
    }
}
