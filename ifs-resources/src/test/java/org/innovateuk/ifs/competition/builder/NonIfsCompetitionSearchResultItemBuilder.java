package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.search.NonIfsCompetitionSearchResultItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class NonIfsCompetitionSearchResultItemBuilder extends CompetitionSearchResultItemBuilder<NonIfsCompetitionSearchResultItem, NonIfsCompetitionSearchResultItemBuilder> {

    private NonIfsCompetitionSearchResultItemBuilder(List<BiConsumer<Integer, NonIfsCompetitionSearchResultItem>> newMultiActions) {
        super(newMultiActions);
    }

    public static NonIfsCompetitionSearchResultItemBuilder newNonIfsCompetitionSearchResultItem() {
        return new NonIfsCompetitionSearchResultItemBuilder(emptyList()).with(uniqueIds());
    }

    public NonIfsCompetitionSearchResultItemBuilder withPublishDate(ZonedDateTime... publishDates) {
        return withArray((publishDate, competition) -> setField("publishDate", publishDate, competition), publishDates);
    }

    @Override
    protected NonIfsCompetitionSearchResultItemBuilder createNewBuilderWithActions(List<BiConsumer<Integer, NonIfsCompetitionSearchResultItem>> actions) {
        return new NonIfsCompetitionSearchResultItemBuilder(actions);
    }

    @Override
    protected NonIfsCompetitionSearchResultItem createInitial() {
        return newInstance(NonIfsCompetitionSearchResultItem.class);
    }
}
