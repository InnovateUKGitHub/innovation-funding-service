package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class YourProjectCostsForm {

    private LabourForm labour = new LabourForm();

    private OverheadForm overhead = new OverheadForm();

    private Map<String, MaterialRowForm> materialRows = new LinkedHashMap<>();

    private Map<String, CapitalUsageRowForm> capitalUsageRows = new LinkedHashMap<>();

    private Map<String, SubcontractingRowForm> subcontractingRows = new LinkedHashMap<>();

    private Map<String, TravelRowForm> travelRows = new LinkedHashMap<>();

    private Map<String, OtherCostRowForm> otherRows = new LinkedHashMap<>();

    private Boolean eligibleAgreement;

    public OverheadForm getOverhead() {
        return overhead;
    }

    public void setOverhead(OverheadForm overhead) {
        this.overhead = overhead;
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
        return labour.getRows().values().stream().map(LabourRowForm::getTotal).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalOverheadCosts() {
        switch (overhead.getRateType()) {
            case NONE:
                return BigDecimal.ZERO;
            case DEFAULT_PERCENTAGE:
                return getTotalLabourCosts().multiply(new BigDecimal("0.2"));
            case TOTAL:
                return Optional.ofNullable(getOverhead().getTotalSpreadsheet()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalMaterialCosts() {
        return materialRows.values().stream().map(MaterialRowForm::getTotal).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalCapitalUsageCosts() {
        return capitalUsageRows.values().stream().map(CapitalUsageRowForm::getTotal).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalSubcontractingCosts() {
        return subcontractingRows.values().stream().map(SubcontractingRowForm::getTotal).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalTravelCosts() {
        return travelRows.values().stream().map(TravelRowForm::getTotal).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalOtherCosts() {
        return otherRows.values().stream().map(OtherCostRowForm::getTotal).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getOrganisationFinanceTotal() {
        return getTotalLabourCosts()
                .add(getTotalOverheadCosts())
                .add(getTotalMaterialCosts())
                .add(getTotalCapitalUsageCosts())
                .add(getTotalSubcontractingCosts())
                .add(getTotalTravelCosts())
                .add(getTotalOtherCosts());
    }
}
