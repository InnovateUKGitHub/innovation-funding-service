package org.innovateuk.ifs.category.builder;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.function.BiConsumer;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.competition.domain.Competition;

public class CategoryLinkBuilder extends BaseBuilder<CompetitionCategoryLink, CategoryLinkBuilder> {

    public static CategoryLinkBuilder newCategoryLink() {
        return new CategoryLinkBuilder(emptyList()).with(uniqueIds());
    }

    private CategoryLinkBuilder(List<BiConsumer<Integer, CompetitionCategoryLink>> multiActions) {
        super(multiActions);
    }

    @Override
    protected CategoryLinkBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionCategoryLink>> actions) {
        return new CategoryLinkBuilder(actions);
    }

    @Override
    protected CompetitionCategoryLink createInitial() {
        return new CompetitionCategoryLink();
    }
    
    public CategoryLinkBuilder withCategory(Category... categories) {
        return withArraySetFieldByReflection("category", categories);
    }

    public CategoryLinkBuilder withCompetition(Competition... competitions) {
        return withArraySetFieldByReflection("competition", competitions);
    }
}
