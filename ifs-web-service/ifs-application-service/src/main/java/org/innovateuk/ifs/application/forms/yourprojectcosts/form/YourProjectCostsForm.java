package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import java.math.BigDecimal;
import java.util.Map;

public class YourProjectCostsForm {

    private Integer workingDaysPerYear;

    private Map<String, LabourRowForm> labourCosts;

    private OverheadForm overhead;

    private Map<String, MaterialRowForm> materialRows;

    private Map<String, CapitalUsageRowForm> capitalUsageRows;

    private Map<String, SubcontractingRowForm> subcontractingRows;

    private Map<String, TravelRowForm> travelRows;

    private Map<String, OtherCostRowForm> otherRows;

    private Boolean eligibleAgreement;

    public Integer getWorkingDaysPerYear() {
        return workingDaysPerYear;
    }

    public void setWorkingDaysPerYear(Integer workingDaysPerYear) {
        this.workingDaysPerYear = workingDaysPerYear;
    }

    public Map<String, LabourRowForm> getLabourCosts() {
        return labourCosts;
    }

    public void setLabourCosts(Map<String, LabourRowForm> labourCosts) {
        this.labourCosts = labourCosts;
    }

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


    /* View methods. */
    public BigDecimal getTotalLabourCosts() {
        return labourCosts.values().stream().map(LabourRowForm::getTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }
}
