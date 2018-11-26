package org.innovateuk.ifs.application.forms.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.*;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

public abstract class AbstractYourProjectCostsSaver {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractYourProjectCostsSaver.class);

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    public ServiceResult<Void> saveType(YourProjectCostsForm form, FinanceRowType type, long targetId, long organisationId) {
        BaseFinanceResource finance = getFinanceResource(targetId, organisationId);
        ValidationMessages messages = new ValidationMessages();

        switch (type) {
            case LABOUR:
                messages.addAll(saveLabourCosts(form.getLabour(), finance));
                break;
            case OVERHEADS:
                messages.addAll(saveOverheads(form.getOverhead(), finance));
                break;
            case CAPITAL_USAGE:
                messages.addAll(saveRows(form.getCapitalUsageRows(), finance));
                break;
            case MATERIALS:
                messages.addAll(saveRows(form.getMaterialRows(), finance));
                break;
            case OTHER_COSTS:
                messages.addAll(saveRows(form.getOtherRows(), finance));
                break;
            case SUBCONTRACTING_COSTS:
                messages.addAll(saveRows(form.getSubcontractingRows(), finance));
                break;
            case TRAVEL:
                messages.addAll(saveRows(form.getTravelRows(), finance));
                break;
        }
        if (messages.getErrors().isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(messages.getErrors());
        }
    }
    public ServiceResult<Void> save(YourProjectCostsForm form, long targetId, long organisationId) {
        BaseFinanceResource finance = getFinanceResource(targetId, organisationId);

        ValidationMessages messages = new ValidationMessages();

        messages.addAll(saveLabourCosts(form.getLabour(), finance));
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

    private ValidationMessages saveLabourCosts(LabourForm labourForm, BaseFinanceResource finance) {
        ValidationMessages messages = new ValidationMessages();

        LabourCostCategory labourCostCategory = (LabourCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.LABOUR);
        labourCostCategory.getWorkingDaysPerYearCostItem().setLabourDays(labourForm.getWorkingDaysPerYear());
        messages.addAll(getFinanceRowService().update(labourCostCategory.getWorkingDaysPerYearCostItem()).getSuccess());
        messages.addAll(saveRows(labourForm.getRows(), finance));
        return messages;
    }

    private ValidationMessages saveOverheads(OverheadForm overhead, BaseFinanceResource finance) {
        OverheadCostCategory overheadCostCategory = (OverheadCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OVERHEADS);
        Overhead overheadCost = (Overhead) overheadCostCategory.getCosts().stream().findFirst().get();

        overheadCost.setRateType(overhead.getRateType());

        if (overhead.getRateType().equals(OverheadRateType.TOTAL)) {
            overheadCost.setRate(ofNullable(overhead.getTotalSpreadsheet()).orElse(0));
        } else {
            overheadCost.setRate(overhead.getRateType().getRate());
        }

        return getFinanceRowService().update(overheadCost).getSuccess();
    }

    private <R extends AbstractCostRowForm> ValidationMessages saveRows(Map<String, R> rows, BaseFinanceResource finance) {
        ValidationMessages messages = new ValidationMessages();

        rows.forEach((id, row) -> {
            if (id.startsWith(UNSAVED_ROW_PREFIX)) {
                if (!row.isBlank()) {
                    FinanceRowItem result = getFinanceRowService().addWithResponse(finance.getId(), row.toCost()).getSuccess();
                    messages.addAll(getFinanceRowService().update(result)); //TODO these two rest calls really could be a single one if the response contained the validation messages.
                }
            } else {
                messages.addAll(getFinanceRowService().update(row.toCost()).getSuccess());
            }
        });

        return messages;
    }

    public void removeRowFromForm(YourProjectCostsForm form, FinanceRowType type, String id) {
        getRowsFromType(form, type).remove(id);
        removeFinanceRow(id);
    }

    public void removeFinanceRow(String id) {
        if (!id.startsWith(UNSAVED_ROW_PREFIX)) {
            getFinanceRowService().delete(Long.valueOf(id)).getSuccess();
        }
    }

    public <R extends AbstractCostRowForm> Map.Entry<String, R> addRowForm(YourProjectCostsForm form, FinanceRowType rowType) throws IllegalAccessException, InstantiationException {
        Class<R> clazz = newRowFromType(rowType);
        R row = clazz.newInstance();
        String costId = generateUnsavedRowId();
        Map<String, R> map = getRowsFromType(form, rowType);
        map.put(costId, row);
        return map.entrySet().stream().filter(entry -> entry.getKey().equals(costId)).findFirst().get();
    }

    private <R extends AbstractCostRowForm> Map<String, R> getRowsFromType(YourProjectCostsForm form, FinanceRowType type) {
        Map<String, ?> map;
        switch (type) {
            case LABOUR:
                map = form.getLabour().getRows();
                break;
            case CAPITAL_USAGE:
                map = form.getCapitalUsageRows();
                break;
            case MATERIALS:
                map = form.getMaterialRows();
                break;
            case OTHER_COSTS:
                map = form.getOtherRows();
                break;
            case SUBCONTRACTING_COSTS:
                map = form.getSubcontractingRows();
                break;
            case TRAVEL:
                map = form.getTravelRows();
                break;
            default:
                throw new RuntimeException("Unknown row type");
        }
        return (Map<String, R>) map;
    }

    private <R extends AbstractCostRowForm> Class<R> newRowFromType(FinanceRowType type) {
        Class<?> clazz;
        switch (type) {
            case LABOUR:
                clazz = LabourRowForm.class;
                break;
            case CAPITAL_USAGE:
                clazz = CapitalUsageRowForm.class;
                break;
            case MATERIALS:
                clazz = MaterialRowForm.class;
                break;
            case OTHER_COSTS:
                clazz = OtherCostRowForm.class;
                break;
            case SUBCONTRACTING_COSTS:
                clazz = SubcontractingRowForm.class;
                break;
            case TRAVEL:
                clazz = TravelRowForm.class;
                break;
            default:
                throw new RuntimeException("Unknown row type");
        }
        return (Class<R>) clazz;
    }

    protected abstract BaseFinanceResource getFinanceResource(long targetId, long organisationId);

    protected abstract FinanceRowRestService getFinanceRowService();
}