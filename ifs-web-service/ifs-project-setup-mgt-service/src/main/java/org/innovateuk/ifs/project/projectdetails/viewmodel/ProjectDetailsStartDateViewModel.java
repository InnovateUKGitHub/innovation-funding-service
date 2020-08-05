package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

/**
 * View model that backs the Project Details - Start Date page
 */
public class ProjectDetailsStartDateViewModel implements BasicProjectDetailsViewModel {
    private Long applicationId;
    private Long projectId;
    private String projectName;
    private long projectDurationInMonths;
    private Long competitionId;
    private boolean ktpCompetition;

    public ProjectDetailsStartDateViewModel(ProjectResource project, CompetitionResource competition) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.projectDurationInMonths = project.getDurationInMonths();
        this.applicationId = project.getApplication();
        this.competitionId = project.getCompetition();
        this.ktpCompetition = competition.isKtp();
    }

    public String getProjectName() {
        return projectName;
    }

    public long getProjectDurationInMonths() {
        return projectDurationInMonths;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }
}
