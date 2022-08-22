package org.innovateuk.ifs.management.notification.viewmodel;


import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.ApplicationDecisionToSendApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SendNotificationsViewModel {

    private List<ApplicationDecisionToSendApplicationResource> applications;
    private long competitionId;
    private String competitionName;
    private long successfulRecipientsCount;
    private long unsuccessfulRecipientsCount;
    private long onHoldRecipientsCount;
    private boolean h2020;
    private boolean includeAssessorsScore;
    private boolean alwaysOpen;
    private boolean horizonEurope;
    private boolean hasAssessmentStage;
    private boolean isDirectAward;

    public SendNotificationsViewModel(List<ApplicationDecisionToSendApplicationResource> applications,
                                      long successfulRecipientsCount,
                                      long unsuccessfulRecipientsCount,
                                      long onHoldRecipientsCount,
                                      CompetitionResource competition,
                                      boolean includeAssessorsScore,
                                      boolean horizonEurope) {

        this.successfulRecipientsCount = successfulRecipientsCount;
        this.unsuccessfulRecipientsCount = unsuccessfulRecipientsCount;
        this.onHoldRecipientsCount = onHoldRecipientsCount;
        this.applications = applications;
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.h2020 = competition.isH2020();
        this.alwaysOpen = competition.isAlwaysOpen();
        this.includeAssessorsScore = includeAssessorsScore;
        this.horizonEurope = horizonEurope;
        this.hasAssessmentStage = competition.isHasAssessmentStage();
        this.isDirectAward = competition.isDirectAward();
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ApplicationDecisionToSendApplicationResource> getApplications() {
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

    public boolean isDirectAward() {
        return isDirectAward;
    }

    public String getPageTitle () {
        return alwaysOpen && hasAssessmentStage
                ? "Send decision notification and release feedback" : "Send decision notification";
    }

    public Map<Long, Decision> getDecisions() {
        return getApplications()
                .stream()
                .collect(Collectors.toMap(
                        ApplicationDecisionToSendApplicationResource::getId,
                        ApplicationDecisionToSendApplicationResource::getDecision
                ));
    }

    public boolean isHorizonEurope() {
        return horizonEurope;
    }
}
