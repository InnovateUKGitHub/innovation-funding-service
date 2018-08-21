package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionResearchCategoryLink;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionResearchCategoryLinkBuilder extends BaseBuilder<CompetitionResearchCategoryLink, CompetitionResearchCategoryLinkBuilder> {

    private CompetitionResearchCategoryLinkBuilder(List<BiConsumer<Integer, CompetitionResearchCategoryLink>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionResearchCategoryLinkBuilder newCompetitionResearchCategoryLink() {
        return new CompetitionResearchCategoryLinkBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionResearchCategoryLinkBuilder withCompetition(Competition... competitions) {
        return withArraySetFieldByReflection("competition", competitions);
    }

    @Override
    protected CompetitionResearchCategoryLinkBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionResearchCategoryLink>> actions) {
        return new CompetitionResearchCategoryLinkBuilder(actions);
    }

    @Override
    protected CompetitionResearchCategoryLink createInitial() {
        return new CompetitionResearchCategoryLink();
    }
}
