package org.innovateuk.ifs.project.activitylog.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

public class ActivityLogViewModel {

    private final long competitionId;
    private final long applicationId;
    private final long projectId;
    private final String projectName;
    private final String competitionName;
    private final String leadPartner;
    private final String otherPartners;
    private final boolean collaborativeProject;

    private final List<ActivityLogEntryViewModel> activities;

    public ActivityLogViewModel(ProjectResource project, String leadPartner, String otherPartners, List<ActivityLogEntryViewModel> activities) {
        this.competitionId = project.getCompetition();
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.competitionName = project.getCompetitionName();
        this.leadPartner = leadPartner;
        this.otherPartners = otherPartners;
        this.activities = activities;
        this.collaborativeProject = project.isCollaborativeProject();
    }

    public List<ActivityLogEntryViewModel> getActivities() {
        return activities;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getLeadPartner() {
        return leadPartner;
    }

    public String getOtherPartners() {
        return otherPartners;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }
}
