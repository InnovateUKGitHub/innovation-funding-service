package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;
import java.util.Set;

public class PreviousCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private Set<String> innovationAreaNames;
    private String competitionTypeName;
    private ZonedDateTime openDate;

    public PreviousCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, Set<String> innovationAreaNames, String competitionTypeName, ZonedDateTime openDate) {
        super(id, name, competitionStatus);
        this.innovationAreaNames = innovationAreaNames;
        this.competitionTypeName = competitionTypeName;
        this.openDate = openDate;
    }

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
    }

    public String getCompetitionTypeName() {
        return competitionTypeName;
    }

    public ZonedDateTime getOpenDate() {
        return openDate;
    }
}
