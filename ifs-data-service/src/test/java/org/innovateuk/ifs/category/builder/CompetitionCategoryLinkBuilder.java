package org.innovateuk.ifs.category.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.CompetitionCategoryLink;
import org.innovateuk.ifs.competition.domain.Competition;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionCategoryLinkBuilder extends BaseBuilder<CompetitionCategoryLink, CompetitionCategoryLinkBuilder> {

    public static CompetitionCategoryLinkBuilder newCompetitionCategoryLink() {
        return new CompetitionCategoryLinkBuilder(emptyList()).with(uniqueIds());
    }

    private CompetitionCategoryLinkBuilder(List<BiConsumer<Integer, CompetitionCategoryLink>> multiActions) {
        super(multiActions);
    }

    @Override
    protected CompetitionCategoryLinkBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionCategoryLink>> actions) {
        return new CompetitionCategoryLinkBuilder(actions);
    }

    @Override
    protected CompetitionCategoryLink createInitial() {
        return new CompetitionCategoryLink();
    }

    public CompetitionCategoryLinkBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public CompetitionCategoryLinkBuilder withCompetition(Competition... competitions) {
        return withArraySetFieldByReflection("competition", competitions);
    }

    public CompetitionCategoryLinkBuilder withCategory(Category... categories) {
        return withArraySetFieldByReflection("category", categories);
    }
}
