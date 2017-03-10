package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<Long, FundingDecision> getFundingDecisions() {
        // TODO: INFUND-8624 - Do this in Java 8
        Map<Long, FundingDecision> fundingDecisions = new HashMap<>();
        for(ApplicationSummaryResource summary : getApplications())
        {
            fundingDecisions.put(summary.getId(), summary.getFundingDecision());
        }
        return fundingDecisions;
    }
}
