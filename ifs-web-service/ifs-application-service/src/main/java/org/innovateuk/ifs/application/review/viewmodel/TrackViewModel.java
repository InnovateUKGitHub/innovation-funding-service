package org.innovateuk.ifs.application.review.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.math.BigDecimal;

public class TrackViewModel implements BaseAnalyticsViewModel {

    private CompetitionResource currentCompetition;
    private ApplicationResource currentApplication;
    private String earlyMetricsUrl;
    private BigDecimal completedQuestionsPercentage;

    public TrackViewModel(CompetitionResource currentCompetition, ApplicationResource currentApplication, String earlyMetricsUrl, BigDecimal completedQuestionsPercentage) {
        this.currentCompetition = currentCompetition;
        this.currentApplication = currentApplication;
        this.earlyMetricsUrl = earlyMetricsUrl;
        this.completedQuestionsPercentage = completedQuestionsPercentage;
    }

    @Override
    public Long getApplicationId() {
        return currentApplication.getId();
    }

    @Override
    public String getCompetitionName() {
        return currentCompetition.getName();
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public void setCurrentCompetition(CompetitionResource currentCompetition) {
        this.currentCompetition = currentCompetition;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public void setCurrentApplication(ApplicationResource currentApplication) {
        this.currentApplication = currentApplication;
    }

    public String getEarlyMetricsUrl() {
        return earlyMetricsUrl;
    }

    public void setEarlyMetricsUrl(String earlyMetricsUrl) {
        this.earlyMetricsUrl = earlyMetricsUrl;
    }

    public BigDecimal getCompletedQuestionsPercentage() {
        return completedQuestionsPercentage;
    }

    public void setCompletedQuestionsPercentage(BigDecimal completedQuestionsPercentage) {
        this.completedQuestionsPercentage = completedQuestionsPercentage;
    }
}
