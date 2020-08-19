package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class YourProjectCostsForm {

    public static final BigDecimal VAT_RATE = BigDecimal.valueOf(20);

    private LabourForm labour = new LabourForm();

    private OverheadForm overhead = new OverheadForm();

    private Map<String, ProcurementOverheadRowForm> procurementOverheadRows = new LinkedHashMap<>();

    private Map<String, MaterialRowForm> materialRows = new LinkedHashMap<>();

    private Map<String, CapitalUsageRowForm> capitalUsageRows = new LinkedHashMap<>();

    private Map<String, SubcontractingRowForm> subcontractingRows = new LinkedHashMap<>();

    private Map<String, TravelRowForm> travelRows = new LinkedHashMap<>();

    private Map<String, OtherCostRowForm> otherRows = new LinkedHashMap<>();

    private VatForm vatForm;

    private Map<String, AssociateSalaryCostRowForm> associateSalaryCostRows = new LinkedHashMap<>();

    private Map<String, AssociateDevelopmentCostRowForm> associateDevelopmentCostRows = new LinkedHashMap<>();

    private Map<String, ConsumablesRowForm> consumableCostRows = new LinkedHashMap<>();

    private Map<String, KnowledgeBaseCostRowForm> knowledgeBaseCostRows = new LinkedHashMap<>();

    private Map<String, AssociateSupportCostRowForm> associateSupportCostRows = new LinkedHashMap<>();

    private Map<String, EstateCostRowForm> estateCostRows = new LinkedHashMap<>();

    private AdditionalCompanyCostForm additionalCompanyCostForm = new AdditionalCompanyCostForm();

    private Boolean eligibleAgreement;

    public VatForm getVatForm() {
        return vatForm;
    }

    public void setVatForm(VatForm vatForm) {
        this.vatForm = vatForm;
    }

    public OverheadForm getOverhead() {
        return overhead;
    }

    public void setOverhead(OverheadForm overhead) {
        this.overhead = overhead;
    }

    public Map<String, ProcurementOverheadRowForm> getProcurementOverheadRows() {
        return procurementOverheadRows;
    }

    public void setProcurementOverheadRows(Map<String, ProcurementOverheadRowForm> procurementOverheadRows) {
        this.procurementOverheadRows = procurementOverheadRows;
    }

    public Map<String, MaterialRowForm> getMaterialRows() {
        return materialRows;
    }

    public void setMaterialRows(Map<String, MaterialRowForm> materialRows) {
        this.materialRows = materialRows;
    }

    public Map<String, CapitalUsageRowForm> getCapitalUsageRows() {
        return capitalUsageRows;
    }

    public void setCapitalUsageRows(Map<String, CapitalUsageRowForm> capitalUsageRows) {
        this.capitalUsageRows = capitalUsageRows;
    }

    public Map<String, SubcontractingRowForm> getSubcontractingRows() {
        return subcontractingRows;
    }

    public void setSubcontractingRows(Map<String, SubcontractingRowForm> subcontractingRows) {
        this.subcontractingRows = subcontractingRows;
    }

    public Map<String, TravelRowForm> getTravelRows() {
        return travelRows;
    }

    public void setTravelRows(Map<String, TravelRowForm> travelRows) {
        this.travelRows = travelRows;
    }

    public Map<String, OtherCostRowForm> getOtherRows() {
        return otherRows;
    }

    public void setOtherRows(Map<String, OtherCostRowForm> otherRows) {
        this.otherRows = otherRows;
    }

    public Boolean getEligibleAgreement() {
        return eligibleAgreement;
    }

    public void setEligibleAgreement(Boolean eligibleAgreement) {
        this.eligibleAgreement = eligibleAgreement;
    }

    public LabourForm getLabour() {
        return labour;
    }

    public void setLabour(LabourForm labour) {
        this.labour = labour;
    }

    public Map<String, AssociateSalaryCostRowForm> getAssociateSalaryCostRows() {
        return associateSalaryCostRows;
    }

    public void setAssociateSalaryCostRows(Map<String, AssociateSalaryCostRowForm> associateSalaryCostRows) {
        this.associateSalaryCostRows = associateSalaryCostRows;
    }

    public Map<String, AssociateSupportCostRowForm> getAssociateSupportCostRows() {
        return associateSupportCostRows;
    }

    public void setAssociateSupportCostRows(Map<String, AssociateSupportCostRowForm> associateSupportCostRows) {
        this.associateSupportCostRows = associateSupportCostRows;
    }

    public Map<String, EstateCostRowForm> getEstateCostRows() {
        return estateCostRows;
    }

    public void setEstateCostRows(Map<String, EstateCostRowForm> estateCostRows) {
        this.estateCostRows = estateCostRows;
    }

    public AdditionalCompanyCostForm getAdditionalCompanyCostForm() {
        return additionalCompanyCostForm;
    }

    public void setAdditionalCompanyCostForm(AdditionalCompanyCostForm additionalCompanyCostForm) {
        this.additionalCompanyCostForm = additionalCompanyCostForm;
    }

    public Map<String, AssociateDevelopmentCostRowForm> getAssociateDevelopmentCostRows() {
        return associateDevelopmentCostRows;
    }

    public void setAssociateDevelopmentCostRows(Map<String, AssociateDevelopmentCostRowForm> associateDevelopmentCostRows) {
        this.associateDevelopmentCostRows = associateDevelopmentCostRows;
    }

    public Map<String, ConsumablesRowForm> getConsumableCostRows() {
        return consumableCostRows;
    }

    public void setConsumableCostRows(Map<String, ConsumablesRowForm> consumableCostRows) {
        this.consumableCostRows = consumableCostRows;
    }

    public Map<String, KnowledgeBaseCostRowForm> getKnowledgeBaseCostRows() {
        return knowledgeBaseCostRows;
    }

    public void setKnowledgeBaseCostRows(Map<String, KnowledgeBaseCostRowForm> knowledgeBaseCostRows) {
        this.knowledgeBaseCostRows = knowledgeBaseCostRows;
    }

    /* View methods. */
    public BigDecimal getVatTotal() {
        return getOrganisationFinanceTotal().multiply(VAT_RATE).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal getProjectVatTotal() {
        return getOrganisationFinanceTotal().add(getVatTotal());
    }

    public BigDecimal getTotalLabourCosts() {
        return labour == null ? BigDecimal.ZERO : calculateTotal(labour.getRows());
    }

    public BigDecimal getTotalAssociateSalaryCosts() {
        return calculateTotal(associateSalaryCostRows);
    }

    public BigDecimal getTotalOverheadCosts() {
        if (overhead != null && overhead.getRateType() != null) {
            switch (overhead.getRateType()) {
                case NONE:
                    return BigDecimal.ZERO;
                case DEFAULT_PERCENTAGE:
                    return getTotalLabourCosts().multiply(new BigDecimal("0.2"));
                case TOTAL:
                    return Optional.ofNullable(getOverhead().getTotalSpreadsheet()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalMaterialCosts() {
        return calculateTotal(materialRows);
    }

    public BigDecimal getTotalProcurementOverheadCosts() {
        return calculateTotal(procurementOverheadRows);
    }

    public BigDecimal getTotalCapitalUsageCosts() {
        return calculateTotal(capitalUsageRows);
    }

    public BigDecimal getTotalSubcontractingCosts() {
        return calculateTotal(subcontractingRows);
    }

    public BigDecimal getTotalTravelCosts() {
        return calculateTotal(travelRows);
    }

    public BigDecimal getTotalOtherCosts() {
        return calculateTotal(otherRows);
    }

    public BigDecimal getTotalAssociateSupportCosts() {
        return calculateTotal(associateSupportCostRows);
    }

    public BigDecimal getTotalAssociateDevelopmentCosts() {
        return calculateTotal(associateDevelopmentCostRows);
    }

    public BigDecimal getTotalConsumableCosts() {
        return calculateTotal(consumableCostRows);
    }

    public BigDecimal getTotalKnowledgeBaseCosts() {
        return calculateTotal(knowledgeBaseCostRows);
    }

    public BigDecimal getTotalEstateCosts() {
        return calculateTotal(estateCostRows);
    }

    public BigDecimal getOrganisationFinanceTotal() {
        return getTotalLabourCosts()
                .add(getTotalOverheadCosts())
                .add(getTotalMaterialCosts())
                .add(getTotalProcurementOverheadCosts())
                .add(getTotalCapitalUsageCosts())
                .add(getTotalSubcontractingCosts())
                .add(getTotalTravelCosts())
                .add(getTotalOtherCosts())
                .add(getTotalAssociateSalaryCosts())
                .add(getTotalAssociateDevelopmentCosts())
                .add(getTotalAssociateSupportCosts())
                .add(getTotalConsumableCosts())
                .add(getTotalKnowledgeBaseCosts())
                .add(getTotalEstateCosts());
    }

    private BigDecimal calculateTotal(Map<String, ? extends AbstractCostRowForm> costRows) {
        return costRows
                .values()
                .stream()
                .map(AbstractCostRowForm::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

    }

    public void recalculateTotals() {
        getLabour().getRows().forEach((id, row) -> {
            LabourCost cost = row.toCost(null);
            row.setTotal(cost.getTotal(getLabour().getWorkingDaysPerYear()));
            row.setRate(cost.getRate(getLabour().getWorkingDaysPerYear()));
        });
        getOverhead().setTotal(getOverhead().getTotal());
        recalculateTotal(getMaterialRows());
        recalculateTotal(getCapitalUsageRows());
        recalculateTotal(getSubcontractingRows());
        recalculateTotal(getTravelRows());
        recalculateTotal(getOtherRows());

        recalculateTotal(getProcurementOverheadRows());

        recalculateTotal(getAssociateSalaryCostRows());
        recalculateTotal(getAssociateDevelopmentCostRows());
        recalculateTotal(getConsumableCostRows());
        recalculateTotal(getKnowledgeBaseCostRows());
        recalculateTotal(getEstateCostRows());
        recalculateTotal(getAssociateSupportCostRows());
    }

    private void recalculateTotal(Map<String, ? extends AbstractCostRowForm> rows) {
        rows.forEach((id, row) -> {
            FinanceRowItem cost = row.toCost(null);
            row.setTotal(cost.getTotal());
        });
    }
}
