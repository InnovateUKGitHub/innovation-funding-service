package org.innovateuk.ifs.competition.resource.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.Set;

public class PreviousCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private final int applications;
    private final int projects;
    private final int completeProjects;

    public PreviousCompetitionSearchResultItem() {
        applications = 0;
        projects = 0;
        completeProjects = 0;
    }

    @JsonCreator
    public PreviousCompetitionSearchResultItem(@JsonProperty("id") long id,
                                               @JsonProperty("name") String name,
                                               @JsonProperty("competitionStatus") CompetitionStatus competitionStatus,
                                               @JsonProperty("competitionTypeName") String competitionTypeName,
                                               @JsonProperty("innovationAreaNames") Set<String> innovationAreaNames,
                                               @JsonProperty("applications") int applications,
                                               @JsonProperty("projects") int projects,
                                               @JsonProperty("completeProjects") int completeProjects) {
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