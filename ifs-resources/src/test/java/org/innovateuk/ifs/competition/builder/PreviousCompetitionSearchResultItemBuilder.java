package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.search.PreviousCompetitionSearchResultItem;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class PreviousCompetitionSearchResultItemBuilder extends CompetitionSearchResultItemBuilder<PreviousCompetitionSearchResultItem, PreviousCompetitionSearchResultItemBuilder> {

    private PreviousCompetitionSearchResultItemBuilder(List<BiConsumer<Integer, PreviousCompetitionSearchResultItem>> newMultiActions) {
        super(newMultiActions);
    }

    public static PreviousCompetitionSearchResultItemBuilder newPreviousCompetitionSearchResultItem() {
        return new PreviousCompetitionSearchResultItemBuilder(emptyList()).with(uniqueIds());
    }

    public PreviousCompetitionSearchResultItemBuilder openDate(Integer... projectsCounts) {
        return withArray((projectsCount, competition) -> setField("projectsCount", projectsCount, competition), projectsCounts);
    }

    @Override
    protected PreviousCompetitionSearchResultItemBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PreviousCompetitionSearchResultItem>> actions) {
        return new PreviousCompetitionSearchResultItemBuilder(actions);
    }

    @Override
    protected PreviousCompetitionSearchResultItem createInitial() {
        return newInstance(PreviousCompetitionSearchResultItem.class);
    }
}
