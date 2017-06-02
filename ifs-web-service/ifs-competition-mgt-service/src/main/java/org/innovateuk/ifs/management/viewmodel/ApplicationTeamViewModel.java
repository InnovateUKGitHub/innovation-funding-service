package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationTeamResource;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamViewModel {
    private String applicationName;
    private ApplicationTeamResource team;
    private long applicationId;
    private long competitionId;

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    String queryParams;

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
        this.competitionId = competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public ApplicationTeamResource getTeam() {
        return team;
    }

    public void setTeam(ApplicationTeamResource team) {
        this.team = team;
    }
}
