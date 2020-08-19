package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.category.AdditionalCompanyCostCategory;

import javax.validation.Valid;
import java.math.BigInteger;

import static java.util.Optional.ofNullable;

public class AdditionalCompanyCostForm {

    @Valid
    private AdditionalCostAndDescription associateSalary;
    @Valid
    private AdditionalCostAndDescription managementSupervision;
    @Valid
    private AdditionalCostAndDescription otherStaff;
    @Valid
    private AdditionalCostAndDescription capitalEquipment;
    @Valid
    private AdditionalCostAndDescription otherCosts;

    public AdditionalCompanyCostForm() {
    }

    public AdditionalCompanyCostForm(AdditionalCompanyCostCategory costCategory) {
        associateSalary = new AdditionalCostAndDescription(costCategory.getAssociateSalary());
        managementSupervision = new AdditionalCostAndDescription(costCategory.getManagementSupervision());
        otherStaff = new AdditionalCostAndDescription(costCategory.getOtherStaff());
        capitalEquipment = new AdditionalCostAndDescription(costCategory.getCapitalEquipment());
        otherCosts = new AdditionalCostAndDescription(costCategory.getOtherCosts());
    }

    public AdditionalCostAndDescription getAssociateSalary() {
        return associateSalary;
    }

    public void setAssociateSalary(AdditionalCostAndDescription associateSalary) {
        this.associateSalary = associateSalary;
    }

    public AdditionalCostAndDescription getManagementSupervision() {
        return managementSupervision;
    }

    public void setManagementSupervision(AdditionalCostAndDescription managementSupervision) {
        this.managementSupervision = managementSupervision;
    }

    public AdditionalCostAndDescription getOtherStaff() {
        return otherStaff;
    }

    public void setOtherStaff(AdditionalCostAndDescription otherStaff) {
        this.otherStaff = otherStaff;
    }

    public AdditionalCostAndDescription getCapitalEquipment() {
        return capitalEquipment;
    }

    public void setCapitalEquipment(AdditionalCostAndDescription capitalEquipment) {
        this.capitalEquipment = capitalEquipment;
    }

    public AdditionalCostAndDescription getOtherCosts() {
        return otherCosts;
    }

    public void setOtherCosts(AdditionalCostAndDescription otherCosts) {
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

    private BigInteger zeroIfNull(AdditionalCostAndDescription cost) {
        return ofNullable(cost)
                .map(AdditionalCostAndDescription::getCost)
                .orElse(BigInteger.ZERO);
    }
}
