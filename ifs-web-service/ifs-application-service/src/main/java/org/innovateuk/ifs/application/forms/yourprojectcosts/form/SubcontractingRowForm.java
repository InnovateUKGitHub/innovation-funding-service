package org.innovateuk.ifs.application.forms.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.SubContractingCost;

import java.math.BigDecimal;

public class SubcontractingRowForm extends AbstractCostRowForm<SubContractingCost> {

    private String name;

    private String country;

    private String role;

    private BigDecimal cost;

    public SubcontractingRowForm() { }

    public SubcontractingRowForm(SubContractingCost cost) {
        super(cost);
        this.name = cost.getName();
        this.role = cost.getRole();
        this.country = cost.getCountry();
        this.cost = cost.getCost();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
