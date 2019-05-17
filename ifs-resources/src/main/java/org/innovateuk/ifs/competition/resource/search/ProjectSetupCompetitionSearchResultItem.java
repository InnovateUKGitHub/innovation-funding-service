package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;
import java.util.Set;

public class ProjectSetupCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private Set<String> innovationAreaNames;
    private Integer projectsCount;
    private ZonedDateTime manageFundingEmailDate;

    private ProjectSetupCompetitionSearchResultItem() {} //for jackson

    public ProjectSetupCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName, Set<String> innovationAreaNames, Integer projectsCount, ZonedDateTime manageFundingEmailDate) {
        super(id, name, competitionStatus, competitionTypeName);
        this.innovationAreaNames = innovationAreaNames;
        this.projectsCount = projectsCount;
        this.manageFundingEmailDate = manageFundingEmailDate;
    }

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
    }

    public Integer getProjectsCount() {
        return projectsCount;
    }

    public ZonedDateTime getManageFundingEmailDate() {
        return manageFundingEmailDate;
    }
}
