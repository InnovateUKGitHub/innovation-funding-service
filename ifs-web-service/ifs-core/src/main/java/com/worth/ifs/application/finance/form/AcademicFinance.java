package com.worth.ifs.application.finance.form;


import com.worth.ifs.application.finance.model.AcademicFinanceFormField;

import java.math.BigDecimal;

public class AcademicFinance {

    AcademicFinanceFormField tsbReference;
    AcademicFinanceFormField incurredStaff;
    AcademicFinanceFormField incurredTravelAndSubsistence;
    AcademicFinanceFormField incurredOtherCosts;
    AcademicFinanceFormField allocatedInvestigators;
    AcademicFinanceFormField allocatedEstatesCosts;
    AcademicFinanceFormField allocatedOtherCosts;
    AcademicFinanceFormField indirectCosts;
    AcademicFinanceFormField exceptionsStaff;
    AcademicFinanceFormField exceptionsOtherCosts;

    public AcademicFinanceFormField getTsbReference() {
        return tsbReference;
    }

    public void setTsbReference(AcademicFinanceFormField tsbReference) {
        this.tsbReference = tsbReference;
    }

    public AcademicFinanceFormField getIncurredStaff() {
        return incurredStaff;
    }

    public void setIncurredStaff(AcademicFinanceFormField incurredStaff) {
        this.incurredStaff = incurredStaff;
    }

    public AcademicFinanceFormField getIncurredTravelAndSubsistence() {
        return incurredTravelAndSubsistence;
    }

    public void setIncurredTravelAndSubsistence(AcademicFinanceFormField incurredTravelAndSubsistence) {
        this.incurredTravelAndSubsistence = incurredTravelAndSubsistence;
    }

    public AcademicFinanceFormField getIncurredOtherCosts() {
        return incurredOtherCosts;
    }

    public void setIncurredOtherCosts(AcademicFinanceFormField incurredOtherCosts) {
        this.incurredOtherCosts = incurredOtherCosts;
    }

    public AcademicFinanceFormField getAllocatedInvestigators() {
        return allocatedInvestigators;
    }

    public void setAllocatedInvestigators(AcademicFinanceFormField allocatedInvestigators) {
        this.allocatedInvestigators = allocatedInvestigators;
    }

    public AcademicFinanceFormField getAllocatedEstatesCosts() {
        return allocatedEstatesCosts;
    }

    public void setAllocatedEstatesCosts(AcademicFinanceFormField allocatedEstatesCosts) {
        this.allocatedEstatesCosts = allocatedEstatesCosts;
    }

    public AcademicFinanceFormField getAllocatedOtherCosts() {
        return allocatedOtherCosts;
    }

    public void setAllocatedOtherCosts(AcademicFinanceFormField allocatedOtherCosts) {
        this.allocatedOtherCosts = allocatedOtherCosts;
    }

    public AcademicFinanceFormField getIndirectCosts() {
        return indirectCosts;
    }

    public void setIndirectCosts(AcademicFinanceFormField indirectCosts) {
        this.indirectCosts = indirectCosts;
    }

    public AcademicFinanceFormField getExceptionsStaff() {
        return exceptionsStaff;
    }

    public void setExceptionsStaff(AcademicFinanceFormField exceptionsStaff) {
        this.exceptionsStaff = exceptionsStaff;
    }

    public AcademicFinanceFormField getExceptionsOtherCosts() {
        return exceptionsOtherCosts;
    }

    public void setExceptionsOtherCosts(AcademicFinanceFormField exceptionsOtherCosts) {
        this.exceptionsOtherCosts = exceptionsOtherCosts;
    }

    public BigDecimal getTotalIncurred() {
        BigDecimal totalIncurred = BigDecimal.ZERO;
        if(incurredStaff!=null && incurredStaff.getCalculatedValue()!=null)
            totalIncurred = totalIncurred.add(incurredStaff.getCalculatedValue());
        if(incurredTravelAndSubsistence!=null && incurredTravelAndSubsistence.getCalculatedValue()!=null)
            totalIncurred = totalIncurred.add(incurredTravelAndSubsistence.getCalculatedValue());
        if(incurredOtherCosts!=null && incurredOtherCosts.getCalculatedValue()!=null)
            totalIncurred = totalIncurred.add(incurredOtherCosts.getCalculatedValue());
        return totalIncurred;
    }

    public BigDecimal getTotalAllocated() {
        BigDecimal totalAllocated = BigDecimal.ZERO;
        if(allocatedInvestigators!=null && allocatedInvestigators.getCalculatedValue()!=null)
            totalAllocated = totalAllocated.add(allocatedInvestigators.getCalculatedValue());
        if(allocatedEstatesCosts!=null && allocatedEstatesCosts.getCalculatedValue()!=null)
            totalAllocated = totalAllocated.add(allocatedEstatesCosts.getCalculatedValue());
        if(allocatedOtherCosts!=null && allocatedOtherCosts.getCalculatedValue()!=null)
            totalAllocated = totalAllocated.add(allocatedOtherCosts.getCalculatedValue());
        return totalAllocated;
    }

    public BigDecimal getTotalExceptions() {
        BigDecimal totalExceptions = BigDecimal.ZERO;
        if(exceptionsStaff!=null && exceptionsStaff.getCalculatedValue()!=null)
            totalExceptions = totalExceptions.add(exceptionsStaff.getCalculatedValue());
        if(exceptionsOtherCosts!=null && exceptionsOtherCosts.getCalculatedValue()!=null)
            totalExceptions = totalExceptions.add(exceptionsOtherCosts.getCalculatedValue());
        return totalExceptions;
    }

    public BigDecimal getTotal() {
        BigDecimal total = getTotalAllocated().add(getTotalExceptions()).add(getTotalIncurred());

        if(getIndirectCosts()!=null)
            total = total.add(getIndirectCosts().getCalculatedValue());
        return total;
    }
}
