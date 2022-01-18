package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.search.LiveCompetitionSearchResultItem;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class LiveCompetitionSearchResultItemBuilder extends CompetitionSearchResultItemBuilder<LiveCompetitionSearchResultItem, LiveCompetitionSearchResultItemBuilder> {

    private LiveCompetitionSearchResultItemBuilder(List<BiConsumer<Integer, LiveCompetitionSearchResultItem>> newMultiActions) {
        super(newMultiActions);
    }

    public static LiveCompetitionSearchResultItemBuilder newLiveCompetitionSearchResultItem() {
        return new LiveCompetitionSearchResultItemBuilder(emptyList()).with(uniqueIds());
    }

    public LiveCompetitionSearchResultItemBuilder withNumberOfApplications(Integer... numberOfApplications) {
        return withArray((numberOfApplication, competition) -> setField("numberOfApplications", numberOfApplication, competition), numberOfApplications);
    }

    @Override
    protected LiveCompetitionSearchResultItemBuilder createNewBuilderWithActions(List<BiConsumer<Integer, LiveCompetitionSearchResultItem>> actions) {
        return new LiveCompetitionSearchResultItemBuilder(actions);
    }

    @Override
    protected LiveCompetitionSearchResultItem createInitial() {
        return newInstance(LiveCompetitionSearchResultItem.class);
    }
}
