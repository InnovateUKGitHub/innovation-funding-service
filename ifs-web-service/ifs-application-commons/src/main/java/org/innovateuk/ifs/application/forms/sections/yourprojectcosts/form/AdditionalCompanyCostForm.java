package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.category.AdditionalCompanyCostCategory;

import java.math.BigInteger;

public class AdditionalCompanyCostForm {

    private BigInteger associateSalary;
    private BigInteger managementSupervision;
    private BigInteger otherStaff;
    private BigInteger capitalEquipment;
    private BigInteger otherCosts;

    public AdditionalCompanyCostForm() {
    }

    public AdditionalCompanyCostForm(AdditionalCompanyCostCategory costCategory) {
        associateSalary = costCategory.getAssociateSalary().getCost();
        managementSupervision = costCategory.getManagementSupervision().getCost();
        otherStaff = costCategory.getOtherStaff().getCost();
        capitalEquipment = costCategory.getCapitalEquipment().getCost();
        otherCosts = costCategory.getOtherCosts().getCost();
    }

    public BigInteger getAssociateSalary() {
        return associateSalary;
    }

    public void setAssociateSalary(BigInteger associateSalary) {
        this.associateSalary = associateSalary;
    }

    public BigInteger getManagementSupervision() {
        return managementSupervision;
    }

    public void setManagementSupervision(BigInteger managementSupervision) {
        this.managementSupervision = managementSupervision;
    }

    public BigInteger getOtherStaff() {
        return otherStaff;
    }

    public void setOtherStaff(BigInteger otherStaff) {
        this.otherStaff = otherStaff;
    }

    public BigInteger getCapitalEquipment() {
        return capitalEquipment;
    }

    public void setCapitalEquipment(BigInteger capitalEquipment) {
        this.capitalEquipment = capitalEquipment;
    }

    public BigInteger getOtherCosts() {
        return otherCosts;
    }

    public void setOtherCosts(BigInteger otherCosts) {
        this.otherCosts = otherCosts;
    }

    /* view methods */
    public BigInteger getTotal() {
        return zeroIfNull(associateSalary)
                .add(zeroIfNull(managementSupervision))
                .add(zeroIfNull(otherStaff))
                .add(zeroIfNull(capitalEquipment))
                .add(zeroIfNull(otherCosts));
    }

    private BigInteger zeroIfNull(BigInteger cost) {
        if (cost == null) {
            return BigInteger.ZERO;
        }
        return cost;
    }
}
