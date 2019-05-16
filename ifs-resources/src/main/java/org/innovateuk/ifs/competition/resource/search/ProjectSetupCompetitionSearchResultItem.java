package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;
import java.util.Set;

public class ProjectSetupCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private Set<String> innovationAreaNames;
    private String competitionTypeName;
    private Integer projectsCount;
    private ZonedDateTime manageFundingEmailDate;

    private ProjectSetupCompetitionSearchResultItem() {} //for jackson

    public ProjectSetupCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, Set<String> innovationAreaNames, String competitionTypeName, Integer projectsCount, ZonedDateTime manageFundingEmailDate) {
        super(id, name, competitionStatus);
        this.innovationAreaNames = innovationAreaNames;
        this.competitionTypeName = competitionTypeName;
        this.projectsCount = projectsCount;
        this.manageFundingEmailDate = manageFundingEmailDate;
    }

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
    }

    public String getCompetitionTypeName() {
        return competitionTypeName;
    }

    public Integer getProjectsCount() {
        return projectsCount;
    }

    public ZonedDateTime getManageFundingEmailDate() {
        return manageFundingEmailDate;
    }
}
