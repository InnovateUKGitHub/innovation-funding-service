package org.innovateuk.ifs.project.spendprofile.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.project.finance.resource.CostCategoryResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SpendProfileTableResource {
    private Boolean markedAsComplete;
    private List<LocalDateResource> months;
    private Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap;
    private Map<Long, BigDecimal> eligibleCostPerCategoryMap;
    private ValidationMessages validationMessages;
    private Map<Long, CostCategoryResource> costCategoryResourceMap;
    private Map<String, List<Map<Long, List<BigDecimal>>>> costCategoryGroupMap;

    public List<LocalDateResource> getMonths() {
        return months;
    }

    public void setMonths(List<LocalDateResource> months) {
        this.months = months;
    }

    public Map<Long, List<BigDecimal>> getMonthlyCostsPerCategoryMap() {
        return monthlyCostsPerCategoryMap;
    }

    public void setMonthlyCostsPerCategoryMap(Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap) {
        this.monthlyCostsPerCategoryMap = monthlyCostsPerCategoryMap;
    }

    public Map<Long, BigDecimal> getEligibleCostPerCategoryMap() {
        return eligibleCostPerCategoryMap;
    }

    public void setEligibleCostPerCategoryMap(Map<Long, BigDecimal> eligibleCostPerCategoryMap) {
        this.eligibleCostPerCategoryMap = eligibleCostPerCategoryMap;
    }

    public Boolean getMarkedAsComplete() {
        return markedAsComplete;
    }

    public void setMarkedAsComplete(Boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }


    public ValidationMessages getValidationMessages() {
        return validationMessages;
    }

    public void setValidationMessages(ValidationMessages validationMessages) {
        this.validationMessages = validationMessages;
    }

    public Map<Long, CostCategoryResource> getCostCategoryResourceMap() {
        return costCategoryResourceMap;
    }

    public void setCostCategoryResourceMap(Map<Long, CostCategoryResource> costCategoryResourceMap) {
        this.costCategoryResourceMap = costCategoryResourceMap;
    }

    public Map<String, List<Map<Long, List<BigDecimal>>>> getCostCategoryGroupMap() {
        return costCategoryGroupMap;
    }

    public void setCostCategoryGroupMap(Map<String, List<Map<Long, List<BigDecimal>>>> costCategoryGroupMap) {
        this.costCategoryGroupMap = costCategoryGroupMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpendProfileTableResource that = (SpendProfileTableResource) o;

        return new EqualsBuilder()
                .append(markedAsComplete, that.markedAsComplete)
                .append(months, that.months)
                .append(monthlyCostsPerCategoryMap, that.monthlyCostsPerCategoryMap)
                .append(eligibleCostPerCategoryMap, that.eligibleCostPerCategoryMap)
                .append(validationMessages, that.validationMessages)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(markedAsComplete)
                .append(months)
                .append(monthlyCostsPerCategoryMap)
                .append(eligibleCostPerCategoryMap)
                .append(validationMessages)
                .toHashCode();
    }
}
