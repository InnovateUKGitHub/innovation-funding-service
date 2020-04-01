package org.innovateuk.ifs.analytics;

public abstract class BaseAnalyticsViewModel {

    private Long applicationId;

    private String competitionName;

    public BaseAnalyticsViewModel(Long applicationId, String competitionName) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }
}
