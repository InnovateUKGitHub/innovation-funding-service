package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.*;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.category.VatCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;

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
        form.setProcurementOverheadRows(procurementOverheadRows(finance));
        form.setLabour(labour(finance));
        form.setCapitalUsageRows(capitalUsageRows(finance));
        form.setMaterialRows(materialRows(finance));
        form.setOtherRows(otherRows(finance));
        form.setSubcontractingRows(subcontractingRows(finance));
        form.setTravelRows(travelRows(finance));
        form.setVatForm(vat(finance));
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

    private Map<String, ProcurementOverheadRowForm> procurementOverheadRows(BaseFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.PROCUREMENT_OVERHEADS);
        if (costCategory != null) {
            Map<String, ProcurementOverheadRowForm> rows = costCategory.getCosts().stream()
                    .map(ProcurementOverhead.class::cast)
                    .map(ProcurementOverheadRowForm::new)
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (shouldAddEmptyRow()) {
                rows.put(generateUnsavedRowId(), new ProcurementOverheadRowForm());
            }
            return rows;
        }
        return new HashMap<>();
    }

    private Map<String, MaterialRowForm> materialRows(BaseFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.MATERIALS);
        if (costCategory != null) {
            Map<String, MaterialRowForm> rows = costCategory.getCosts().stream()
                    .map(Materials.class::cast)
                    .map(MaterialRowForm::new)
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (shouldAddEmptyRow()) {
                rows.put(generateUnsavedRowId(), new MaterialRowForm());
            }
            return rows;
        }
        return new HashMap<>();
    }

    private Map<String, CapitalUsageRowForm> capitalUsageRows(BaseFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.CAPITAL_USAGE);
        if (costCategory != null) {

            Map<String, CapitalUsageRowForm> rows = costCategory.getCosts().stream()
                    .map(CapitalUsage.class::cast)
                    .map(CapitalUsageRowForm::new)
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (shouldAddEmptyRow()) {
                rows.put(generateUnsavedRowId(), new CapitalUsageRowForm());
            }
            return rows;
        }
        return new HashMap<>();
    }

    private Map<String, OtherCostRowForm> otherRows(BaseFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS);

        if (costCategory != null) {
            Map<String, OtherCostRowForm> rows = costCategory.getCosts().stream()
                    .map(OtherCost.class::cast)
                    .map(OtherCostRowForm::new)
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (shouldAddEmptyRow()) {
                rows.put(generateUnsavedRowId(), new OtherCostRowForm());
            }
            return rows;
        }
        return new HashMap<>();
    }

    private Map<String, SubcontractingRowForm> subcontractingRows(BaseFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS);
        if (costCategory != null) {
            Map<String, SubcontractingRowForm> rows = costCategory.getCosts().stream()
                    .map(SubContractingCost.class::cast)
                    .map(SubcontractingRowForm::new)
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (shouldAddEmptyRow()) {
                rows.put(generateUnsavedRowId(), new SubcontractingRowForm());
            }
            return rows;
        }
        return new HashMap<>();
    }

    private Map<String, TravelRowForm> travelRows(BaseFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL);
        if (costCategory != null) {
            Map<String, TravelRowForm> rows = costCategory.getCosts().stream()
                    .map(TravelCost.class::cast)
                    .map(TravelRowForm::new)
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (shouldAddEmptyRow()) {
                rows.put(generateUnsavedRowId(), new TravelRowForm());
            }
            return rows;
        }
        return new HashMap<>();
    }

     private VatForm vat(BaseFinanceResource finance) {
        VatCostCategory costCategory = (VatCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.VAT);
         if (costCategory != null) {
             Vat vat = costCategory.getCosts().stream().findFirst().map(Vat.class::cast).orElseThrow(() -> new IFSRuntimeException("Missing expected Vat cost"));
            return new VatForm(vat);
         }
         return new VatForm();
    }

    protected abstract BaseFinanceResource getFinanceResource(long targetId, long organisationId);

    protected abstract boolean shouldAddEmptyRow();

    protected abstract Optional<FileEntryResource> overheadFile(long costId);
}
