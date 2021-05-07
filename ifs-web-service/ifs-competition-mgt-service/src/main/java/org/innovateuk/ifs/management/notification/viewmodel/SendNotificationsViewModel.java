package org.innovateuk.ifs.management.notification.viewmodel;


import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingDecisionToSendApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SendNotificationsViewModel {

    private List<FundingDecisionToSendApplicationResource> applications;
    private long competitionId;
    private String competitionName;
    private long successfulRecipientsCount;
    private long unsuccessfulRecipientsCount;
    private long onHoldRecipientsCount;
    private boolean h2020;
    private boolean includeAssessorsScore;
    private boolean alwaysOpen;

    public SendNotificationsViewModel(List<FundingDecisionToSendApplicationResource> applications,
                                      long successfulRecipientsCount,
                                      long unsuccessfulRecipientsCount,
                                      long onHoldRecipientsCount,
                                      CompetitionResource competition,
                                      boolean includeAssessorsScore) {

        this.successfulRecipientsCount = successfulRecipientsCount;
        this.unsuccessfulRecipientsCount = unsuccessfulRecipientsCount;
        this.onHoldRecipientsCount = onHoldRecipientsCount;
        this.applications = applications;
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.h2020 = competition.isH2020();
        this.alwaysOpen = competition.isAlwaysOpen();
        this.includeAssessorsScore = includeAssessorsScore;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<FundingDecisionToSendApplicationResource> getApplications() {
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

    public boolean isIncludeAssessorsScore() {
        return includeAssessorsScore;
    }

    public boolean isAlwaysOpen() {
        return alwaysOpen;
    }

    public String getPageTitle () {
        return alwaysOpen ? "Send decision notification and release feedback" : "Send decision notification";
    }

    public Map<Long, FundingDecision> getFundingDecisions() {
        return getApplications()
                .stream()
                .collect(Collectors.toMap(
                        FundingDecisionToSendApplicationResource::getId,
                        FundingDecisionToSendApplicationResource::getFundingDecision
                ));
    }
}
