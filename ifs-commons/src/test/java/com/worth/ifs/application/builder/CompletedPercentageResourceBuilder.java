package com.worth.ifs.application.builder;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.CompletedPercentageResource;

public class CompletedPercentageResourceBuilder extends BaseBuilder<CompletedPercentageResource, CompletedPercentageResourceBuilder> {

    private CompletedPercentageResourceBuilder(List<BiConsumer<Integer, CompletedPercentageResource>> multiActions) {
        super(multiActions);
    }

    public static CompletedPercentageResourceBuilder newCompletedPercentageResource() {
        return new CompletedPercentageResourceBuilder(emptyList());
    }

    @Override
    protected CompletedPercentageResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompletedPercentageResource>> actions) {
        return new CompletedPercentageResourceBuilder(actions);
    }

    @Override
    protected CompletedPercentageResource createInitial() {
        return new CompletedPercentageResource();
    }

    public CompletedPercentageResourceBuilder withCompletedPercentage(BigDecimal... completedPercentages) {
        return withArray((completedPercentage, completedPercentageResource) -> completedPercentageResource.setCompletedPercentage(completedPercentage), completedPercentages);
    }

}
