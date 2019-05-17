package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;
import java.util.Set;

public class PreviousCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private ZonedDateTime openDate;

    private PreviousCompetitionSearchResultItem() {} //for jackson

    public PreviousCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName, Set<String> innovationAreaNames, ZonedDateTime openDate) {
        super(id, name, competitionStatus, competitionTypeName, innovationAreaNames);
        this.openDate = openDate;
    }

    public ZonedDateTime getOpenDate() {
        return openDate;
    }
}
