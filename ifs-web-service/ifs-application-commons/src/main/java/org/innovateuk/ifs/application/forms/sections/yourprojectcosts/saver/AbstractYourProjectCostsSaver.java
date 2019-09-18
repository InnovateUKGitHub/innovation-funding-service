package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.*;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.category.VatCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

public abstract class AbstractYourProjectCostsSaver extends AsyncAdaptor {

    public ServiceResult<Void> saveType(YourProjectCostsForm form, FinanceRowType type, long targetId, long organisationId) {
        try {
            BaseFinanceResource finance = getFinanceResource(targetId, organisationId);
            ValidationMessages messages = new ValidationMessages();

            switch (type) {
                case LABOUR:
                    messages.addAll(saveLabourCosts(form.getLabour(), finance).get());
                    break;
                case OVERHEADS:
                    messages.addAll(saveOverheads(form.getOverhead(), finance).get());
                    break;
                case CAPITAL_USAGE:
                    messages.addAll(saveRows(form.getCapitalUsageRows(), finance).get());
                    break;
                case MATERIALS:
                    messages.addAll(saveRows(form.getMaterialRows(), finance).get());
                    break;
                case OTHER_COSTS:
                    messages.addAll(saveRows(form.getOtherRows(), finance).get());
                    break;
                case SUBCONTRACTING_COSTS:
                    messages.addAll(saveRows(form.getSubcontractingRows(), finance).get());
                    break;
                case TRAVEL:
                    messages.addAll(saveRows(form.getTravelRows(), finance).get());
                    break;
                case PROCUREMENT_OVERHEADS:
                    messages.addAll(saveRows(form.getProcurementOverheadRows(), finance).get());
                    break;
                case VAT:
                    messages.addAll(saveVat(form.getVatForm(), finance).get());
                    break;
            }
            if (messages.getErrors().isEmpty()) {
                return serviceSuccess();
            } else {
                return serviceFailure(messages.getErrors());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IFSRuntimeException(e, emptyList());
        }
    }

    public ServiceResult<Void> save(YourProjectCostsForm form, long targetId, long organisationId) {
        BaseFinanceResource finance = getFinanceResource(targetId, organisationId);

        List<CompletableFuture<ValidationMessages>> futures = new ArrayList<>();

        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.LABOUR)) {
            futures.add(saveLabourCosts(form.getLabour(), finance));
        }
        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.OVERHEADS)) {
            futures.add(saveOverheads(form.getOverhead(), finance));
        }
        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.MATERIALS)) {
            futures.add(saveRows(form.getMaterialRows(), finance));
        }
        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.CAPITAL_USAGE)) {
            futures.add(saveRows(form.getCapitalUsageRows(), finance));
        }
        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.SUBCONTRACTING_COSTS)) {
            futures.add(saveRows(form.getSubcontractingRows(), finance));
        }
        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.TRAVEL)) {
            futures.add(saveRows(form.getTravelRows(), finance));
        }
        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.OTHER_COSTS)) {
            futures.add(saveRows(form.getOtherRows(), finance));
        }
        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.PROCUREMENT_OVERHEADS)) {
            futures.add(saveRows(form.getProcurementOverheadRows(), finance));
        }
        if (finance.getFinanceOrganisationDetails().containsKey(FinanceRowType.VAT)) {
            futures.add(saveVat(form.getVatForm(), finance));
        }

        ValidationMessages messages = new ValidationMessages();

        awaitAll(futures)
                .thenAccept(messages::addAll);

        if (messages.getErrors().isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(messages.getErrors());
        }
    }

    private CompletableFuture<ValidationMessages> saveLabourCosts(LabourForm labourForm, BaseFinanceResource finance) {
        return async(() -> {
            ValidationMessages messages = new ValidationMessages();

            LabourCostCategory labourCostCategory = (LabourCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.LABOUR);
            labourCostCategory.getWorkingDaysPerYearCostItem().setLabourDays(labourForm.getWorkingDaysPerYear());
            messages.addAll(getFinanceRowService().update(labourCostCategory.getWorkingDaysPerYearCostItem()).getSuccess());
            messages.addAll(saveRows(labourForm.getRows(), finance).get());
            return messages;
        });
    }

    private CompletableFuture<ValidationMessages> saveOverheads(OverheadForm overhead, BaseFinanceResource finance) {
        return async(() -> {
            OverheadCostCategory overheadCostCategory = (OverheadCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OVERHEADS);
            Overhead overheadCost = (Overhead) overheadCostCategory.getCosts().stream().findFirst().get();

            overheadCost.setRateType(overhead.getRateType());

            if (overhead.getRateType().equals(OverheadRateType.TOTAL)) {
                overheadCost.setRate(ofNullable(overhead.getTotalSpreadsheet()).orElse(0));
            } else {
                overheadCost.setRate(overhead.getRateType().getRate());
            }

            return getFinanceRowService().update(overheadCost).getSuccess();
        });
    }

    private CompletableFuture<ValidationMessages> saveVat(VatForm vatForm, BaseFinanceResource finance) {
        return async(() -> {
            ValidationMessages messages = new ValidationMessages();
            VatCostCategory vatCategory = (VatCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.VAT);
            Vat vatCost = (Vat) vatCategory.getCosts().stream().findFirst().get();

            vatCost.setRegistered(vatForm.getRegistered());

            messages.addAll(getFinanceRowService().update(vatCost).getSuccess());

            return messages;
        });
    }

    private <R extends AbstractCostRowForm> CompletableFuture<ValidationMessages> saveRows(Map<String, R> rows, BaseFinanceResource finance) {
        return async(() -> {
            ValidationMessages messages = new ValidationMessages();

            rows.forEach((id, row) -> {
                if (id.startsWith(UNSAVED_ROW_PREFIX)) {
                    if (!row.isBlank()) {
                        FinanceRowItem result = getFinanceRowService().create(row.toCost(finance.getId())).getSuccess();
                        messages.addAll(getFinanceRowService().update(result)); //TODO these two rest calls really could be a single one if the response contained the validation messages.
                    }
                } else {
                    messages.addAll(getFinanceRowService().update(row.toCost(finance.getId())).getSuccess());
                }
            });

            return messages;
        });

    }

    public void removeRowFromForm(YourProjectCostsForm form, String id) {
        //Try to remove key from all the maps. Will have a random uuid attached.
        form.getLabour().getRows().remove(id);
        form.getMaterialRows().remove(id);
        form.getCapitalUsageRows().remove(id);
        form.getSubcontractingRows().remove(id);
        form.getTravelRows().remove(id);
        form.getOtherRows().remove(id);
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
            case PROCUREMENT_OVERHEADS:
                map = form.getProcurementOverheadRows();
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
            case PROCUREMENT_OVERHEADS:
                clazz = ProcurementOverheadRowForm.class;
                break;
            default:
                throw new RuntimeException("Unknown row type");
        }
        return (Class<R>) clazz;
    }

    protected abstract BaseFinanceResource getFinanceResource(long targetId, long organisationId);

    protected abstract FinanceRowRestService getFinanceRowService();
}