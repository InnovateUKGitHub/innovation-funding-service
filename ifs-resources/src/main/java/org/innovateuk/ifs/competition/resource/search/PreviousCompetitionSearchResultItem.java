package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.Set;

public class PreviousCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private Integer applications;
    private Integer projects;
    private Integer completeProjects;

    private PreviousCompetitionSearchResultItem() {} //for jackson

    public PreviousCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName, Set<String> innovationAreaNames, Integer applications, Integer projects, Integer completeProjects) {
        super(id, name, competitionStatus, competitionTypeName, innovationAreaNames);
        this.applications = applications;
        this.projects = projects;
        this.completeProjects = completeProjects;
    }

    public Integer getApplications() {
        return applications;
    }

    public Integer getProjects() {
        return projects;
    }

    public Integer getCompleteProjects() {
        return completeProjects;
    }
}
