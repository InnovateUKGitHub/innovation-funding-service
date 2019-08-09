package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.VAT;

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

    private VAT vat;

    private Boolean eligibleAgreement;

    public VAT getVat() {
        return vat;
    }

    public void setVat(VAT vat) {
        this.vat = vat;
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

    /* View methods. */
    public BigDecimal getTotalLabourCosts() {
        return labour == null ? BigDecimal.ZERO : calculateTotal(labour.getRows());
    }

    public BigDecimal getVatTotal() {
        if (vat == null || Boolean.FALSE.equals(vat.getRegistered())) {
            return BigDecimal.ZERO;
        }
        return getOrganisationFinanceTotal().multiply(VAT_RATE).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal getProjectVatTotal() {
        if (vat == null || Boolean.FALSE.equals(vat.getRegistered())) {
            return BigDecimal.ZERO;
        }
        return getOrganisationFinanceTotal().add(getVatTotal());
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

    public BigDecimal getOrganisationFinanceTotal() {
        return getTotalLabourCosts()
                .add(getTotalOverheadCosts())
                .add(getTotalMaterialCosts())
                .add(getTotalProcurementOverheadCosts())
                .add(getTotalCapitalUsageCosts())
                .add(getTotalSubcontractingCosts())
                .add(getTotalTravelCosts())
                .add(getTotalOtherCosts());
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
}
