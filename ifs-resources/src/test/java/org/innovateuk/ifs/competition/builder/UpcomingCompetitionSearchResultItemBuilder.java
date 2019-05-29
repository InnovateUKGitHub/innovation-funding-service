package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.search.UpcomingCompetitionSearchResultItem;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class UpcomingCompetitionSearchResultItemBuilder extends CompetitionSearchResultItemBuilder<UpcomingCompetitionSearchResultItem, UpcomingCompetitionSearchResultItemBuilder> {

    private UpcomingCompetitionSearchResultItemBuilder(List<BiConsumer<Integer, UpcomingCompetitionSearchResultItem>> newMultiActions) {
        super(newMultiActions);
    }

    public static UpcomingCompetitionSearchResultItemBuilder newUpcomingCompetitionSearchResultItem() {
        return new UpcomingCompetitionSearchResultItemBuilder(emptyList()).with(uniqueIds());
    }

    public UpcomingCompetitionSearchResultItemBuilder withStartDateDisplay(String... startDates) {
        return withArray((startDate, competition) -> setField("startDateDisplay", startDate, competition), startDates);
    }

    @Override
    protected UpcomingCompetitionSearchResultItemBuilder createNewBuilderWithActions(List<BiConsumer<Integer, UpcomingCompetitionSearchResultItem>> actions) {
        return new UpcomingCompetitionSearchResultItemBuilder(actions);
    }

    @Override
    protected UpcomingCompetitionSearchResultItem createInitial() {
        return newInstance(UpcomingCompetitionSearchResultItem.class);
    }
}
