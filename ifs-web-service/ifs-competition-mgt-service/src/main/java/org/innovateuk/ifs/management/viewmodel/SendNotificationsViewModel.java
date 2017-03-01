package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;

import java.util.Collections;
import java.util.List;

public class SendNotificationsViewModel {

    private List<ApplicationSummaryResource> applications;
    private long competitionId;
    private String competitionName;
    private CompetitionInFlightStatsViewModel keyStatistics;


    public SendNotificationsViewModel(List<ApplicationSummaryResource> applications, CompetitionInFlightStatsViewModel keyStatistics, long competitionId, String competitionName) {
        this.keyStatistics = keyStatistics;
        this.applications = applications;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
    }

    public List<ApplicationSummaryResource> getResults() {
        return applications;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ApplicationSummaryResource> getApplications() {
        return applications != null ? applications : Collections.emptyList();
    }

    public CompetitionInFlightStatsViewModel getKeyStatistics() {
        return keyStatistics;
    }
}
