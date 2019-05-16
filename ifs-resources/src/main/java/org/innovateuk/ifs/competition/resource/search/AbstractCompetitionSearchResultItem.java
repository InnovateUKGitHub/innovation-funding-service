package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

public abstract class AbstractCompetitionSearchResultItem implements CompetitionSearchResultItem {

    private long id;
    private String name;
    private CompetitionStatus competitionStatus;

    public AbstractCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus) {
        this.id = id;
        this.name = name;
        this.competitionStatus = competitionStatus;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }
}
