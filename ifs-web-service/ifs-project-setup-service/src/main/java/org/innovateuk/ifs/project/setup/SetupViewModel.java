package org.innovateuk.ifs.project.setup;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.time.ZonedDateTime;

public class SetupViewModel {

    private final long competitionId;
    private final String competitionName;
    private final long projectId;
    private final String projectName;
    private final ZonedDateTime submittedDate;

    public SetupViewModel(CompetitionResource competitionResource, ProjectResource projectResource, ZonedDateTime submittedDate) {
        this.competitionId = competitionResource.getId();
        this.competitionName = competitionResource.getName();
        this.projectId = projectResource.getId();
        this.projectName = projectResource.getName();
        this.submittedDate = submittedDate;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public ZonedDateTime getSubmittedDate() {
        return submittedDate;
    }

    public String getProjectName() {
        return projectName;
    }
}
