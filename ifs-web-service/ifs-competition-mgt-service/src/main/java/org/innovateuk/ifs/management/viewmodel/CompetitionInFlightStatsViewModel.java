package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.competition.resource.*;

/**
 * Holder of key statistics for the in-flight dashboard
 */
public class CompetitionInFlightStatsViewModel {

    private int statOne;
    private int statTwo;
    private Integer statThree;
    private Integer statFour;
    private Integer statFive;
    private Integer statSix;
    private boolean canManageFundingNotifications = true;
    private CompetitionStatus status;
    private boolean canReleaseFeedback = true;

    public CompetitionInFlightStatsViewModel() {

    }

    public CompetitionInFlightStatsViewModel(CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource, CompetitionStatus competitionStatus) {
        this.statOne = keyStatisticsResource.getAssessorsInvited();
        this.statTwo = keyStatisticsResource.getAssessorsAccepted();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionOpenKeyStatisticsResource keyStatisticsResource, CompetitionStatus competitionStatus) {
        this.statOne = keyStatisticsResource.getAssessorsInvited();
        this.statTwo = keyStatisticsResource.getAssessorsAccepted();
        this.statThree = keyStatisticsResource.getApplicationsPerAssessor();
        this.statFour = keyStatisticsResource.getApplicationsStarted();
        this.statFive = keyStatisticsResource.getApplicationsPastHalf();
        this.statSix = keyStatisticsResource.getApplicationsSubmitted();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionClosedKeyStatisticsResource keyStatisticsResource, CompetitionStatus competitionStatus) {
        this.statOne = keyStatisticsResource.getApplicationsRequiringAssessors();
        this.statTwo = keyStatisticsResource.getAssignmentCount();
        this.statThree = keyStatisticsResource.getAssessorsWithoutApplications();
        this.statFour = keyStatisticsResource.getAssessorsInvited();
        this.statFive = keyStatisticsResource.getAssessorsAccepted();
        this.statSix = keyStatisticsResource.getApplicationsPerAssessor();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource, CompetitionStatus competitionStatus) {
        this.statOne = keyStatisticsResource.getAssignmentCount();
        this.statTwo = keyStatisticsResource.getAssignmentsWaiting();
        this.statThree = keyStatisticsResource.getAssignmentsAccepted();
        this.statFour = keyStatisticsResource.getAssessmentsStarted();
        this.statFive = keyStatisticsResource.getAssessmentsSubmitted();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionFundedKeyStatisticsResource keyStatisticsResource, CompetitionStatus competitionStatus) {
        this.statOne = keyStatisticsResource.getApplicationsSubmitted();
        this.statTwo = keyStatisticsResource.getApplicationsFunded();
        this.statThree = keyStatisticsResource.getApplicationsNotFunded();
        this.statFour = keyStatisticsResource.getApplicationsOnHold();
        this.statFive = keyStatisticsResource.getApplicationsNotifiedOfDecision();
        this.statSix = keyStatisticsResource.getApplicationsAwaitingDecision();
        this.canManageFundingNotifications = keyStatisticsResource.isCanManageFundingNotifications();
        this.canReleaseFeedback = keyStatisticsResource.isCanReleaseFeedback();
        this.canManageFundingNotifications = statTwo > 0 || statThree > 0 || statFour > 0;
        this.status = competitionStatus;
    }

    public CompetitionStatus getStatus() {
        return status;
    }

    public int getStatOne() {
        return statOne;
    }

    public int getStatTwo() {
        return statTwo;
    }

    public Integer getStatThree() {
        return statThree;
    }

    public Integer getStatFour() {
        return statFour;
    }

    public Integer getStatFive() {
        return statFive;
    }

    public Integer getStatSix() {
        return statSix;
    }

    public boolean isCanManageFundingNotifications() {
        return canManageFundingNotifications;
    }

    public boolean isCanReleaseFeedback() {
        return canReleaseFeedback;
    }
}