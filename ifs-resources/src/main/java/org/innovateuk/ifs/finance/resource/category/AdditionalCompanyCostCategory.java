package org.innovateuk.ifs.finance.resource.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;

/**
 * {@code AdditionalCompanyCostCategory} implementation for {@link FinanceRowCostCategory}.
 * Total doesn't count toward the costs.
 */
public class AdditionalCompanyCostCategory implements FinanceRowCostCategory {

    private AdditionalCompanyCost associateSalary;
    private AdditionalCompanyCost managementSupervision;
    private AdditionalCompanyCost otherStaff;
    private AdditionalCompanyCost capitalEquipment;
    private AdditionalCompanyCost otherCosts;

    private BigDecimal total;

    public AdditionalCompanyCost getAssociateSalary() {
        return associateSalary;
    }

    public void setAssociateSalary(AdditionalCompanyCost associateSalary) {
        this.associateSalary = associateSalary;
    }

    public AdditionalCompanyCost getManagementSupervision() {
        return managementSupervision;
    }

    public void setManagementSupervision(AdditionalCompanyCost managementSupervision) {
        this.managementSupervision = managementSupervision;
    }

    public AdditionalCompanyCost getOtherStaff() {
        return otherStaff;
    }

    public void setOtherStaff(AdditionalCompanyCost otherStaff) {
        this.otherStaff = otherStaff;
    }

    public AdditionalCompanyCost getCapitalEquipment() {
        return capitalEquipment;
    }

    public void setCapitalEquipment(AdditionalCompanyCost capitalEquipment) {
        this.capitalEquipment = capitalEquipment;
    }

    public AdditionalCompanyCost getOtherCosts() {
        return otherCosts;
    }

    public void setOtherCosts(AdditionalCompanyCost otherCosts) {
        this.otherCosts = otherCosts;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    @JsonIgnore
    public List<FinanceRowItem> getCosts() {
        return newArrayList(associateSalary, managementSupervision, otherStaff, capitalEquipment, otherStaff);
    }

    @Override
    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public void calculateTotal() {
        total = getCosts().stream()
                .map(AdditionalCompanyCost.class::cast)
                .map(AdditionalCompanyCost::getTotal)
                .map(cost -> ofNullable(cost).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void addCost(FinanceRowItem costItem) {
        AdditionalCompanyCost cost = (AdditionalCompanyCost) costItem;
        switch (cost.getType()) {
            case ASSOCIATE_SALARY:
                associateSalary = cost;
                break;
            case MANAGEMENT_SUPERVISION:
                managementSupervision = cost;
                break;
            case OTHER_STAFF:
                otherStaff = cost;
                break;
            case CAPITAL_EQUIPMENT:
                capitalEquipment = cost;
                break;
            case OTHER_COSTS:
                otherCosts = cost;
                break;
        }
    }

    @Override
    public boolean excludeFromTotalCost() {
        return true;
    }
}
