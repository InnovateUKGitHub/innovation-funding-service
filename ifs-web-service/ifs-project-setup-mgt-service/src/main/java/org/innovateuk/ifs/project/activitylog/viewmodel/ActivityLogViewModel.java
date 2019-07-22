package org.innovateuk.ifs.project.activitylog.viewmodel;

import java.util.List;

public class ActivityLogViewModel {

    private final long competitionId;
    private final long applicationId;
    private final long projectId;
    private final String projectName;
    private final String competitionName;
    private final String leadPartner;
    private final String otherPartners;

    private final List<ActivityLogEntryViewModel> activities;

    public ActivityLogViewModel(long competitionId, long applicationId, long projectId, String projectName, String competitionName, String leadPartner, String otherPartners, List<ActivityLogEntryViewModel> activities) {
        this.competitionId = competitionId;
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.competitionName = competitionName;
        this.leadPartner = leadPartner;
        this.otherPartners = otherPartners;
        this.activities = activities;
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
}
