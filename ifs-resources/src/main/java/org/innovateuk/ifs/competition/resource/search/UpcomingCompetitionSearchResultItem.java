package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.Set;

public class UpcomingCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private String startDateDisplay;

    private UpcomingCompetitionSearchResultItem() {} //for jackson

    public UpcomingCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName, Set<String> innovationAreaNames, String startDateDisplay) {
        super(id, name, competitionStatus, competitionTypeName, innovationAreaNames);
        this.startDateDisplay = startDateDisplay;
    }

    public String getStartDateDisplay() {
        return startDateDisplay;
    }
}
