package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.time.LocalDate;
import java.util.List;

/**
 * View model that backs the Project Details - Start Date page
 */
public class ProjectDetailsStartDateViewModel implements BasicProjectDetailsViewModel {
    private Long applicationId;
    private Long projectId;
    private String projectName;
    private LocalDate targetStartDate;
    private long projectDurationInMonths;
    private long competitionId;
    private List<Long> projectUsers;
    private boolean ktpCompetition;
    private boolean procurementCompetition;

    public ProjectDetailsStartDateViewModel(ProjectResource project, CompetitionResource competitionResource) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.applicationId = project.getApplication();
        this.targetStartDate = project.getTargetStartDate();
        this.projectDurationInMonths = project.getDurationInMonths();
        this.competitionId = project.getCompetition();
        this.projectUsers = project.getProjectUsers();
        this.ktpCompetition = competitionResource.isKtp();
        this.procurementCompetition = competitionResource.isProcurement();
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public LocalDate getTargetStartDate() {
        return targetStartDate;
    }

    public long getProjectDurationInMonths() {
        return projectDurationInMonths;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public List<Long> getProjectUsers() {
        return projectUsers;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }
}
