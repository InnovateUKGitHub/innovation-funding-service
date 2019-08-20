package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.Set;

public class PreviousCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private final int applications;
    private final int projects;
    private final int completeProjects;

    private PreviousCompetitionSearchResultItem() {
        applications = 0;
        projects = 0;
        completeProjects = 0;
    }

    public PreviousCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName, Set<String> innovationAreaNames, int applications, int projects, int completeProjects) {
        super(id, name, competitionStatus, competitionTypeName, innovationAreaNames);
        this.applications = applications;
        this.projects = projects;
        this.completeProjects = completeProjects;
    }

    public int getApplications() {
        return applications;
    }

    public int getProjects() {
        return projects;
    }

    public int getCompleteProjects() {
        return completeProjects;
    }
}