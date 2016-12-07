package com.worth.ifs.project.resource;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.project.finance.resource.CostCategoryResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;

/**
 *
 **/
public final class SpendProfileTableResourceBuilder extends BaseBuilder<SpendProfileTableResource, SpendProfileTableResourceBuilder> {
    private Boolean markedAsComplete;
    /*
         * Dynamically holds the months for the duration of the project
         */
    private List<LocalDateResource> months;
    /*
         * Holds the cost per category for each month, the first entry in the list representing the first month and so on.
         */
    private Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap;
    private Map<Long, BigDecimal> eligibleCostPerCategoryMap;
    private ValidationMessages validationMessages;
    private Map<Long, CostCategoryResource> costCategoryResourceMap;
    private Map<String, List<Map<Long, List<BigDecimal>>>> costCategoryGroupMap;

    private SpendProfileTableResourceBuilder() {
    }

    public static SpendProfileTableResourceBuilder newSpendProfileTableResource() {
        return new SpendProfileTableResourceBuilder();
    }


    public SpendProfileTableResourceBuilder withMarkedAsComplete(Boolean... markedAsCompletes) {
        return withArray((markedAsComplete, spendProfileResource) -> setField("markedAsComplete", markedAsComplete, spendProfileResource), markedAsCompletes);
    }

    public SpendProfileTableResourceBuilder withMonths(List<LocalDateResource>... months) {
        return withArray((month, spendProfileResource) -> setField("month", month, spendProfileResource), months);
    }

    public SpendProfileTableResourceBuilder withMonthlyCostsPerCategoryMap(Map<Long, List<BigDecimal>>... monthlyCostsPerCategoryMaps) {
        return withArray((monthlyCostsPerCategoryMap, spendProfileResource) -> setField("monthlyCostsPerCategoryMap", monthlyCostsPerCategoryMap, spendProfileResource), monthlyCostsPerCategoryMaps);
    }

    public SpendProfileTableResourceBuilder withEligibleCostPerCategoryMap(Map<Long, BigDecimal>... eligibleCostPerCategoryMaps) {
        return withArray((eligibleCostPerCategoryMap, spendProfileResource) -> setField("eligibleCostPerCategoryMap", eligibleCostPerCategoryMap, spendProfileResource), eligibleCostPerCategoryMaps);
    }

    public SpendProfileTableResourceBuilder withValidationMessages(ValidationMessages... validationMessages) {
        return withArray((validationMessage, spendProfileResource) -> setField("validationMessage", validationMessage, spendProfileResource), validationMessages);
    }

    public SpendProfileTableResourceBuilder withCostCategoryResourceMap(Map<Long, CostCategoryResource>... costCategoryResourceMaps) {
        return withArray((costCategoryResourceMap, spendProfileResource) -> setField("costCategoryResourceMap", costCategoryResourceMap, spendProfileResource), costCategoryResourceMaps);
    }

    public SpendProfileTableResourceBuilder withCostCategoryGroupMap(Map<String, List<Map<Long, List<BigDecimal>>>>... costCategoryGroupMaps) {
        return withArray((costCategoryGroupMap, spendProfileResource) -> setField("costCategoryGroupMap", costCategoryGroupMap, spendProfileResource), costCategoryGroupMaps);
    }

    public SpendProfileTableResource build() {
        SpendProfileTableResource spendProfileTableResource = new SpendProfileTableResource();
        spendProfileTableResource.setMarkedAsComplete(markedAsComplete);
        spendProfileTableResource.setMonths(months);
        spendProfileTableResource.setMonthlyCostsPerCategoryMap(monthlyCostsPerCategoryMap);
        spendProfileTableResource.setEligibleCostPerCategoryMap(eligibleCostPerCategoryMap);
        spendProfileTableResource.setValidationMessages(validationMessages);
        spendProfileTableResource.setCostCategoryResourceMap(costCategoryResourceMap);
        spendProfileTableResource.setCostCategoryGroupMap(costCategoryGroupMap);
        return spendProfileTableResource;
    }

    @Override
    protected SpendProfileTableResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SpendProfileTableResource>> actions) {
        return new SpendProfileTableResourceBuilder();
    }

    @Override
    protected SpendProfileTableResource createInitial() {
        return new SpendProfileTableResource();
    }
}
