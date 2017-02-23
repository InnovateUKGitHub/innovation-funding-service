package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.category.domain.ResearchCategory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ResearchCategoryBuilder extends CategoryBuilder<ResearchCategory, ResearchCategoryBuilder> {

    protected ResearchCategoryBuilder(List<BiConsumer<Integer, ResearchCategory>> multiActions) {
        super(multiActions);
    }

    public static ResearchCategoryBuilder newResearchCategory() {
        return new ResearchCategoryBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ResearchCategoryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ResearchCategory>> actions) {
        return new ResearchCategoryBuilder(actions);
    }

    @Override
    protected ResearchCategory createInitial() {
        return new ResearchCategory();
    }
}
