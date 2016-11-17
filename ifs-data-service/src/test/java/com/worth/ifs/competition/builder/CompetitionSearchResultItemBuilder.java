package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSearchResultItem;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionSearchResultItemBuilder extends BaseBuilder<CompetitionSearchResultItem, CompetitionSearchResultItemBuilder> {

    private CompetitionSearchResultItemBuilder(List<BiConsumer<Integer, CompetitionSearchResultItem>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionSearchResultItemBuilder newCompetitionSearchResultItem() {
        return new CompetitionSearchResultItemBuilder(emptyList()).with(uniqueIds());
    }

    public CompetitionSearchResultItemBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public CompetitionSearchResultItemBuilder withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public CompetitionSearchResultItemBuilder withStartDateDisplay(String... startDate) {
        return withArray((date, competition) -> competition.setStartDateDisplay(date), startDate);
    }

    public CompetitionSearchResultItemBuilder withInnovationAreaName(String... innovationAreaName) {
        return withArray((name, competition) -> competition.setInnovationAreaName(name), innovationAreaName);
    }

    public CompetitionSearchResultItemBuilder withNumberOfApplications(Integer... numberOfApplications) {
        return withArray((number, competition) -> competition.setNumberOfApplications(number), numberOfApplications);
    }

    public CompetitionSearchResultItemBuilder withCompetitionStatus(CompetitionResource.Status... competitionStatus) {
        return withArray((status, competition) -> competition.setCompetitionStatus(status), competitionStatus);
    }

    public CompetitionSearchResultItemBuilder withCompetitionTypeName(String... competitionTypeName) {
        return withArray((name, competition) -> competition.setCompetitionTypeName(name), competitionTypeName);
    }

    public CompetitionSearchResultItemBuilder withProjectsCount(Integer... projectsCount) {
        return withArray((number, competition) -> competition.setProjectsCount(number), projectsCount);
    }

    @Override
    protected CompetitionSearchResultItemBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionSearchResultItem>> actions) {
        return new CompetitionSearchResultItemBuilder(actions);
    }

    @Override
    protected CompetitionSearchResultItem createInitial() {
        return newInstance(CompetitionSearchResultItem.class);
    }
}
