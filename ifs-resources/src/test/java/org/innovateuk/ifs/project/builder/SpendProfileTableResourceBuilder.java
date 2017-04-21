package org.innovateuk.ifs.project.builder;


import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.project.finance.resource.CostCategoryResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 *
 **/
public final class SpendProfileTableResourceBuilder extends BaseBuilder<SpendProfileTableResource, SpendProfileTableResourceBuilder> {

    private SpendProfileTableResourceBuilder(List<BiConsumer<Integer, SpendProfileTableResource>> multiActions) {
        super(multiActions);
    }

    public static SpendProfileTableResourceBuilder newSpendProfileTableResource() {
        return new SpendProfileTableResourceBuilder(emptyList()).with(uniqueIds());
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

    @Override
    protected SpendProfileTableResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SpendProfileTableResource>> actions) {
        return new SpendProfileTableResourceBuilder(actions);
    }

    @Override
    protected SpendProfileTableResource createInitial() {
        return new SpendProfileTableResource();
    }
}
