package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ResearchCategoryResourceBuilder extends CategoryResourceBuilder<ResearchCategoryResource, ResearchCategoryResourceBuilder> {
    protected ResearchCategoryResourceBuilder(List<BiConsumer<Integer, ResearchCategoryResource>> multiActions) {
        super(multiActions);
    }

    public static ResearchCategoryResourceBuilder newResearchCategoryResource() {
        return new ResearchCategoryResourceBuilder(emptyList())
                .with(uniqueIds())
                .with(idBasedNames("Research category "));
    }

    @Override
    protected ResearchCategoryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ResearchCategoryResource>> actions) {
        return new ResearchCategoryResourceBuilder(actions);
    }

    @Override
    protected ResearchCategoryResource createInitial() {
        return new ResearchCategoryResource();
    }
}
