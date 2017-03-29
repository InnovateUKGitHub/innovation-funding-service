package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SendNotificationsViewModel {

    private List<ApplicationSummaryResource> applications;
    private long competitionId;
    private String competitionName;
    private long successfulRecipientsCount;
    private long unsuccessfulRecipientsCount;
    private long onHoldRecipientsCount;

    public SendNotificationsViewModel(List<ApplicationSummaryResource> applications, long successfulRecipientsCount, long unsuccessfulRecipientsCount, long onHoldRecipientsCount, long competitionId, String competitionName) {
        this.successfulRecipientsCount = successfulRecipientsCount;
        this.unsuccessfulRecipientsCount = unsuccessfulRecipientsCount;
        this.onHoldRecipientsCount = onHoldRecipientsCount;
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

    public long getSuccessfulRecipientsCount() {
        return successfulRecipientsCount;
    }

    public long getUnsuccessfulRecipientsCount() {
        return unsuccessfulRecipientsCount;
    }

    public long getOnHoldRecipientsCount() {
        return onHoldRecipientsCount;
    }

    public Map<Long, FundingDecision> getFundingDecisions() {

        return getApplications()
                .stream()
                .collect(Collectors.toMap(
                        ApplicationSummaryResource::getId,
                        summary -> summary.getFundingDecision()));
    }
}
