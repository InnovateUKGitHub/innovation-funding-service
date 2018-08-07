package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class CompetitionResearchCategoryLinkResourceBuilder extends
        BaseBuilder<CompetitionResearchCategoryLinkResource, CompetitionResearchCategoryLinkResourceBuilder> {

    private CompetitionResearchCategoryLinkResourceBuilder(List<BiConsumer<Integer, CompetitionResearchCategoryLinkResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionResearchCategoryLinkResourceBuilder newCompetitionResearchCategoryLinkResource() {
        return new CompetitionResearchCategoryLinkResourceBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionResearchCategoryLinkResourceBuilder withCompetition(CompetitionResource... competitions) {
        return withArraySetFieldByReflection("competition", competitions);
    }

    @Override
    protected CompetitionResearchCategoryLinkResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionResearchCategoryLinkResource>> actions) {
        return new CompetitionResearchCategoryLinkResourceBuilder(actions);
    }

    @Override
    protected CompetitionResearchCategoryLinkResource createInitial() {
        return new CompetitionResearchCategoryLinkResource();
    }
}
