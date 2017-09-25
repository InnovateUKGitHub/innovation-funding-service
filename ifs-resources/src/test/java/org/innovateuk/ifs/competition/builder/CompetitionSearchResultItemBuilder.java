package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public CompetitionSearchResultItemBuilder withInnovationAreaNames(Set<String>... innovationAreaNames) {
        return withArray((names, competition) -> competition.setInnovationAreaNames(names), innovationAreaNames);
    }

    public CompetitionSearchResultItemBuilder withNumberOfApplications(Integer... numberOfApplications) {
        return withArray((number, competition) -> competition.setNumberOfApplications(number), numberOfApplications);
    }

    public CompetitionSearchResultItemBuilder withCompetitionStatus(CompetitionStatus... competitionStatus) {
        return withArray((status, competition) -> competition.setCompetitionStatus(status), competitionStatus);
    }

    public CompetitionSearchResultItemBuilder withCompetitionTypeName(String... competitionTypeName) {
        return withArray((name, competition) -> competition.setCompetitionTypeName(name), competitionTypeName);
    }

    public CompetitionSearchResultItemBuilder withProjectsCount(Integer... projectsCount) {
        return withArray((number, competition) -> competition.setProjectsCount(number), projectsCount);
    }

    public CompetitionSearchResultItemBuilder withOpenDate(ZonedDateTime... openDates) {
        return withArray((openDate, competition) -> competition.setOpenDate(openDate), openDates);
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
