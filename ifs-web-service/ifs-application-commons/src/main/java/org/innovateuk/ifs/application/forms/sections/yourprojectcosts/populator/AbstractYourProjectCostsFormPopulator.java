package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.*;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

public abstract class AbstractYourProjectCostsFormPopulator {

    public YourProjectCostsForm populateForm(long targetId, Long organisationId) {
        YourProjectCostsForm form = new YourProjectCostsForm();
        BaseFinanceResource finance = getFinanceResource(targetId, organisationId);

        form.setOverhead(overhead(finance));
        form.setLabour(labour(finance));
        form.setCapitalUsageRows(toRows(finance, FinanceRowType.CAPITAL_USAGE,
                CapitalUsageRowForm.class, CapitalUsage.class));
        form.setMaterialRows(toRows(finance, FinanceRowType.MATERIALS,
                MaterialRowForm.class,  Materials.class));
        form.setOtherRows(toRows(finance, FinanceRowType.OTHER_COSTS,
                OtherCostRowForm.class, OtherCost.class));
        form.setSubcontractingRows(toRows(finance, FinanceRowType.SUBCONTRACTING_COSTS,
                SubcontractingRowForm.class, SubContractingCost.class));
        form.setTravelRows(toRows(finance, FinanceRowType.TRAVEL,
                TravelRowForm.class, TravelCost.class));

        form.setVatForm(vat(finance));
        form.setProcurementOverheadRows(toRows(finance, FinanceRowType.PROCUREMENT_OVERHEADS,
                ProcurementOverheadRowForm.class, ProcurementOverhead.class));

        form.setAssociateSalaryCostRows(toRows(finance, FinanceRowType.ASSOCIATE_SALARY_COSTS,
                AssociateSalaryCostRowForm.class, AssociateSalaryCost.class));
        form.setAssociateDevelopmentCostRows(toRows(finance, FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS,
                AssociateDevelopmentCostRowForm.class, AssociateDevelopmentCost.class));
        form.setConsumableCostRows(toRows(finance, FinanceRowType.CONSUMABLES,
                ConsumablesRowForm.class, Consumable.class));
        form.setKnowledgeBaseCostRows(toRows(finance, FinanceRowType.KNOWLEDGE_BASE,
                KnowledgeBaseCostRowForm.class, KnowledgeBaseCost.class));
        form.setAssociateSupportCostRows(toRows(finance, FinanceRowType.ASSOCIATE_SUPPORT,
                AssociateSupportCostRowForm.class, AssociateSupportCost.class));
        form.setEstateCostRows(toRows(finance, FinanceRowType.ESTATE_COSTS,
                EstateCostRowForm.class, EstateCost.class));
        form.setAdditionalCompanyCostForm(additionalCompanyCostForm(finance));

        return form;
    }

    private LabourForm labour(BaseFinanceResource finance) {
        LabourCostCategory costCategory = (LabourCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR);
        LabourForm labourForm = new LabourForm();
        if (costCategory != null) {
            costCategory.calculateTotal();
            labourForm.setWorkingDaysPerYear(costCategory.getWorkingDaysPerYear());
            labourForm.setRows(labourCosts(costCategory));
        }
        return labourForm;
    }

    private OverheadForm overhead(BaseFinanceResource finance) {
        OverheadCostCategory costCategory = (OverheadCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS);
        if (costCategory != null) {
            Overhead overhead = costCategory.getCosts().stream().findFirst().map(Overhead.class::cast).orElseThrow(() -> new IFSRuntimeException("Missing expected overheads cost."));
            String filename = overheadFile(overhead.getId()).map(FileEntryResource::getName).orElse(null);
            return new OverheadForm(overhead, filename);
        }
        return new OverheadForm();
    }

    private Map<String, LabourRowForm> labourCosts(LabourCostCategory costCategory) {
        Map<String, LabourRowForm> rows = costCategory.getCosts().stream()
                .map(LabourCost.class::cast)
                .map(LabourRowForm::new)
                .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));

        if (shouldAddEmptyRow()) {
            rows.put(generateUnsavedRowId(), new LabourRowForm());
        }
        return rows;
    }

     private VatForm vat(BaseFinanceResource finance) {
        VatCostCategory costCategory = (VatCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.VAT);
         if (costCategory != null) {
             Vat vat = costCategory.getCosts().stream().findFirst().map(Vat.class::cast).orElseThrow(() -> new IFSRuntimeException("Missing expected Vat cost"));
            return new VatForm(vat);
         }
         return new VatForm();
    }

    private AdditionalCompanyCostForm additionalCompanyCostForm(BaseFinanceResource finance) {
        AdditionalCompanyCostCategory costCategory = (AdditionalCompanyCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.ADDITIONAL_COMPANY_COSTS);
        if (costCategory != null) {
            return new AdditionalCompanyCostForm(costCategory);
        }
        return null;
    }

    private <C extends AbstractFinanceRowItem, F extends AbstractCostRowForm<C>> Map<String, F> toRows(BaseFinanceResource finance, FinanceRowType financeRowType, Class<F> formClazz, Class<C> costClazz) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(financeRowType);

        if (costCategory != null) {
            Map<String, F> rows = costCategory.getCosts().stream()
                    .map((cost) -> (C) cost)
                    .map(cost -> {
                        try {
                            return formClazz.getConstructor(costClazz).newInstance(cost);
                        } catch (NoSuchMethodException |
                                IllegalAccessException |
                                InstantiationException |
                                InvocationTargetException e) {
                            throw new IFSRuntimeException(e);
                        }
                    })
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (shouldAddEmptyRow()) {
                try {
                    rows.put(generateUnsavedRowId(), formClazz.newInstance());
                } catch (IllegalAccessException |
                        InstantiationException e) {
                    throw new IFSRuntimeException(e);
                }
            }
            return rows;
        }
        return new HashMap<>();
    }

    protected abstract BaseFinanceResource getFinanceResource(long targetId, long organisationId);

    protected abstract boolean shouldAddEmptyRow();

    protected abstract Optional<FileEntryResource> overheadFile(long costId);
}
