package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.Set;

public abstract class AbstractCompetitionSearchResultItem implements CompetitionSearchResultItem {

    private long id;
    private String name;
    private CompetitionStatus competitionStatus;
    private String competitionTypeName;
    private Set<String> innovationAreaNames;

    AbstractCompetitionSearchResultItem() {}

    public AbstractCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName, Set<String> innovationAreaNames) {
        this.id = id;
        this.name = name;
        this.competitionStatus = competitionStatus;
        this.competitionTypeName = competitionTypeName;
        this.innovationAreaNames = innovationAreaNames;
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

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
    }
}
