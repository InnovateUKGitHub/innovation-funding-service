package org.innovateuk.ifs.management.notification.viewmodel;


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
    private boolean h2020;

    public SendNotificationsViewModel(List<ApplicationSummaryResource> applications,
                                      long successfulRecipientsCount,
                                      long unsuccessfulRecipientsCount,
                                      long onHoldRecipientsCount,
                                      long competitionId,
                                      String competitionName,
                                      boolean h2020) {

        this.successfulRecipientsCount = successfulRecipientsCount;
        this.unsuccessfulRecipientsCount = unsuccessfulRecipientsCount;
        this.onHoldRecipientsCount = onHoldRecipientsCount;
        this.applications = applications;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.h2020 = h2020;
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

    public boolean isH2020() {
        return h2020;
    }

    public Map<Long, FundingDecision> getFundingDecisions() {

        return getApplications()
                .stream()
                .collect(Collectors.toMap(
                        ApplicationSummaryResource::getId,
                        ApplicationSummaryResource::getFundingDecision
                ));
    }
}
