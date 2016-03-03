package com.worth.ifs.application.finance.form;

import java.math.BigDecimal;

public class AcademicFinance {

    String tsbReference;
    BigDecimal incurredStaff;
    BigDecimal incurredTravelAndSubsistence;
    BigDecimal incurredOtherCosts;
    BigDecimal allocatedInvestigators;
    BigDecimal allocatedEstatesCosts;
    BigDecimal allocatedOtherCosts;
    BigDecimal indirectCosts;
    BigDecimal exceptionsStaff;
    BigDecimal exceptionsOtherCosts;

    public String getTsbReference() {
        return tsbReference;
    }

    public void setTsbReference(String tsbReference) {
        this.tsbReference = tsbReference;
    }

    public BigDecimal getIncurredStaff() {
        return incurredStaff;
    }

    public void setIncurredStaff(BigDecimal incurredStaff) {
        this.incurredStaff = incurredStaff;
    }

    public BigDecimal getIncurredTravelAndSubsistence() {
        return incurredTravelAndSubsistence;
    }

    public void setIncurredTravelAndSubsistence(BigDecimal incurredTravelAndSubsistence) {
        this.incurredTravelAndSubsistence = incurredTravelAndSubsistence;
    }

    public BigDecimal getIncurredOtherCosts() {
        return incurredOtherCosts;
    }

    public void setIncurredOtherCosts(BigDecimal incurredOtherCosts) {
        this.incurredOtherCosts = incurredOtherCosts;
    }

    public BigDecimal getAllocatedInvestigators() {
        return allocatedInvestigators;
    }

    public void setAllocatedInvestigators(BigDecimal allocatedInvestigators) {
        this.allocatedInvestigators = allocatedInvestigators;
    }

    public BigDecimal getAllocatedEstatesCosts() {
        return allocatedEstatesCosts;
    }

    public void setAllocatedEstatesCosts(BigDecimal allocatedEstatesCosts) {
        this.allocatedEstatesCosts = allocatedEstatesCosts;
    }

    public BigDecimal getAllocatedOtherCosts() {
        return allocatedOtherCosts;
    }

    public void setAllocatedOtherCosts(BigDecimal allocatedOtherCosts) {
        this.allocatedOtherCosts = allocatedOtherCosts;
    }

    public BigDecimal getIndirectCosts() {
        return indirectCosts;
    }

    public void setIndirectCosts(BigDecimal indirectCosts) {
        this.indirectCosts = indirectCosts;
    }

    public BigDecimal getExceptionsStaff() {
        return exceptionsStaff;
    }

    public void setExceptionsStaff(BigDecimal exceptionsStaff) {
        this.exceptionsStaff = exceptionsStaff;
    }

    public BigDecimal getExceptionsOtherCosts() {
        return exceptionsOtherCosts;
    }

    public void setExceptionsOtherCosts(BigDecimal exceptionsOtherCosts) {
        this.exceptionsOtherCosts = exceptionsOtherCosts;
    }

    public BigDecimal getTotalIncurred() {
        return incurredStaff.add(incurredTravelAndSubsistence).add(incurredOtherCosts);
    }

    public BigDecimal getTotalAllocated() {
        return allocatedInvestigators.add(allocatedEstatesCosts).add(allocatedOtherCosts);
    }

    public BigDecimal getTotalExceptions() {
        return exceptionsStaff.add(exceptionsOtherCosts);
    }

    public BigDecimal getTota() {
        return getTotalIncurred().add(getTotalAllocated()).add(getTotalExceptions()).add(getIndirectCosts());
    }

}
