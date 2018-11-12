package org.innovateuk.ifs.application.forms.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.*;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm.EMPTY_ROW_ID;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Component
public class YourProjectCostsSaver {

    private final static Logger LOG = LoggerFactory.getLogger(YourProjectCostsSaver.class);

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private DefaultFinanceRowRestService financeRowRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private QuestionRestService questionRestService;

    public ServiceResult<Void> save(long applicationId, YourProjectCostsForm form, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();

        ValidationMessages messages = new ValidationMessages();

        messages.addAll(saveLabourCosts(form.getLabourCosts(), form.getWorkingDaysPerYear(), finance));
        messages.addAll(saveOverheads(form.getOverhead(), finance));
        messages.addAll(saveRows(form.getMaterialRows(), finance));
        messages.addAll(saveRows(form.getCapitalUsageRows(), finance));
        messages.addAll(saveRows(form.getSubcontractingRows(), finance));
        messages.addAll(saveRows(form.getTravelRows(), finance));
        messages.addAll(saveRows(form.getOtherRows(), finance));

        if (messages.getErrors().isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(messages.getErrors());
        }
    }

    private ValidationMessages saveOverheads(OverheadForm overhead, ApplicationFinanceResource finance) {
        OverheadCostCategory overheadCostCategory = (OverheadCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OVERHEADS);
        Overhead overheadCost = (Overhead) overheadCostCategory.getCosts().stream().findFirst().get();

        overheadCost.setRateType(overhead.getRateType());
        overheadCost.setRate(overhead.getRateType().equals(OverheadRateType.TOTAL) ? overhead.getTotalSpreadsheet() : 0);

        return financeRowRestService.update(overheadCost).getSuccess();
    }

    private ValidationMessages saveLabourCosts(Map<String, LabourRowForm> labourCosts, Integer workingDaysPerYear, ApplicationFinanceResource finance) {
        ValidationMessages messages = new ValidationMessages();

        LabourCostCategory labourCostCategory = (LabourCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.LABOUR);
        labourCostCategory.getWorkingDaysPerYearCostItem().setLabourDays(workingDaysPerYear);
        messages.addAll(financeRowRestService.update(labourCostCategory.getWorkingDaysPerYearCostItem()).getSuccess());
        messages.addAll(saveRows(labourCosts, finance));
        return messages;

    }

    private <R extends AbstractCostRowForm> ValidationMessages saveRows(Map<String, R> rows, ApplicationFinanceResource finance) {
        ValidationMessages messages = new ValidationMessages();

        rows.forEach((id, row) -> {
            if (EMPTY_ROW_ID.equals(id)) {
                if (!row.isBlank()) {
                    messages.addAll(financeRowRestService.add(finance.getId(), getQuestionId(row.getRowType(), finance), row.toCost()).getSuccess());
                }
            } else {
                messages.addAll(financeRowRestService.update(row.toCost()).getSuccess());
            }
        });

        return messages;
    }

    private Long getQuestionId(FinanceRowType type, ApplicationFinanceResource finance) {
        ApplicationResource applicationResource = applicationRestService.getApplicationById(finance.getApplication()).getSuccess();
        QuestionResource questionResource = questionRestService.getQuestionByCompetitionIdAndFormInputType(applicationResource.getCompetition(), type.getFormInputType()).getSuccess();
        return questionResource.getId();
    }

    public void removeRowFromForm(YourProjectCostsForm form, FinanceRowType type, String id) {
        getRowsFromType(form, type).remove(id);
        removeFinanceRow(id);
    }

    public void removeFinanceRow(String id) {
        if (!EMPTY_ROW_ID.equals(id)) {
            financeRowRestService.delete(Long.valueOf(id)).getSuccess();
        }
    }

    public <R extends AbstractCostRowForm> R addRowForm(YourProjectCostsForm form, FinanceRowType rowType, Long applicationId, UserResource user) throws IllegalAccessException, InstantiationException {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisation.getId()).getSuccess();

        Class<R> clazz = newRowFromType(rowType);
        R row = clazz.newInstance();
        Long costId = financeRowRestService.addWithResponse(finance.getId(), row.toCost()).getSuccess().getId();
        row.setCostId(costId);
        Map<String, R> map = getRowsFromType(form, rowType);
        map.put(String.valueOf(costId), row);
        return row;
    }

    private <R extends AbstractCostRowForm> Map<String, R> getRowsFromType(YourProjectCostsForm form, FinanceRowType type) {
        switch (type) {
            case LABOUR:
                return (Map<String, R>) form.getLabourCosts();
            case CAPITAL_USAGE:
                return (Map<String, R>) form.getCapitalUsageRows();
            case MATERIALS:
                return (Map<String, R>) form.getMaterialRows();
            case OTHER_COSTS:
                return (Map<String, R>) form.getOtherRows();
            case SUBCONTRACTING_COSTS:
                return (Map<String, R>) form.getSubcontractingRows();
            case TRAVEL:
                return (Map<String, R>) form.getTravelRows();
            default:
                throw new RuntimeException("Unknown row type");
        }
    }

    private <R extends AbstractCostRowForm> Class<R> newRowFromType(FinanceRowType type) {
        switch (type) {
            case LABOUR:
                return (Class<R>) LabourRowForm.class;
            case CAPITAL_USAGE:
                return (Class<R>) CapitalUsageRowForm.class;
            case MATERIALS:
                return (Class<R>) MaterialRowForm.class;
            case OTHER_COSTS:
                return (Class<R>) OtherCostRowForm.class;
            case SUBCONTRACTING_COSTS:
                return (Class<R>) SubcontractingRowForm.class;
            case TRAVEL:
                return (Class<R>) TravelRowForm.class;
            default:
                throw new RuntimeException("Unknown row type");
        }
    }
}
