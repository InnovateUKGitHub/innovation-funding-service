package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

public abstract class AbstractCompetitionSearchResultItem implements CompetitionSearchResultItem {

    private long id;
    private String name;
    private CompetitionStatus competitionStatus;
    private String competitionTypeName;

    AbstractCompetitionSearchResultItem() {}

    public AbstractCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName) {
        this.id = id;
        this.name = name;
        this.competitionStatus = competitionStatus;
        this.competitionTypeName = competitionTypeName;
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

    public String getCompetitionTypeName() {
        return competitionTypeName;
    }
}
