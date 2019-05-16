package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.Set;

public class UpcomingCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private Set<String> innovationAreaNames;
    private String competitionTypeName;
    private String startDateDisplay;

    private UpcomingCompetitionSearchResultItem() {} //for jackson

    public UpcomingCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, Set<String> innovationAreaNames, String competitionTypeName, String startDateDisplay) {
        super(id, name, competitionStatus);
        this.innovationAreaNames = innovationAreaNames;
        this.competitionTypeName = competitionTypeName;
        this.startDateDisplay = startDateDisplay;
    }

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
    }

    public String getStartDateDisplay() {
        return startDateDisplay;
    }

    public String getCompetitionTypeName() {
        return competitionTypeName;
    }
}
