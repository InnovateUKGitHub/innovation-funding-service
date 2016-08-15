package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.resource.SpendProfileResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class SpendProfileResourceBuilder extends BaseBuilder<SpendProfileResource, SpendProfileResourceBuilder> {


    private SpendProfileResourceBuilder(List<BiConsumer<Integer, SpendProfileResource>> multiActions) {
        super(multiActions);
    }

    public static SpendProfileResourceBuilder newSpendProfileResource() {
        return new SpendProfileResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected SpendProfileResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SpendProfileResource>> actions) {
        return new SpendProfileResourceBuilder(actions);
    }

    @Override
    protected SpendProfileResource createInitial() {
        return new SpendProfileResource();
    }
}
