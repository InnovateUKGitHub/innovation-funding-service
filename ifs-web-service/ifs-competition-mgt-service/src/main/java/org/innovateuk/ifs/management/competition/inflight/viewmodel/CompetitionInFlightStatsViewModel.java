package org.innovateuk.ifs.management.competition.inflight.viewmodel;

import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
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
    private boolean displayAssessorStats;

    public CompetitionInFlightStatsViewModel() {
    }

    public CompetitionInFlightStatsViewModel(CompetitionReadyToOpenKeyAssessmentStatisticsResource
                                                     keyStatisticsResource, CompetitionStatus competitionStatus, CompetitionCompletionStage completionStage) {
        this.statOne = keyStatisticsResource.getAssessorsInvited();
        this.statTwo = keyStatisticsResource.getAssessorsAccepted();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
        this.displayAssessorStats = CompetitionCompletionStage.COMPETITION_CLOSE.equals(completionStage);
    }

    public CompetitionInFlightStatsViewModel(CompetitionOpenKeyApplicationStatisticsResource
                                                     competitionOpenKeyApplicationStatisticsResource,
                                             CompetitionOpenKeyAssessmentStatisticsResource
                                                     competitionOpenKeyAssessmentStatisticsResource,
                                             CompetitionStatus competitionStatus,
                                             CompetitionFundedKeyApplicationStatisticsResource fundedKeyApplicationStatisticsResource,
                                                     CompetitionCompletionStage completionStage) {
        this.statOne = competitionOpenKeyAssessmentStatisticsResource.getAssessorsInvited();
        this.statTwo = competitionOpenKeyAssessmentStatisticsResource.getAssessorsAccepted();
        this.statThree = competitionOpenKeyApplicationStatisticsResource.getApplicationsPerAssessor();
        this.statFour = competitionOpenKeyApplicationStatisticsResource.getApplicationsStarted();
        this.statFive = competitionOpenKeyApplicationStatisticsResource.getApplicationsPastHalf();
        this.statSix = competitionOpenKeyApplicationStatisticsResource.getApplicationsSubmitted();
        this.canManageFundingNotifications = fundedKeyApplicationStatisticsResource.isCanManageFundingNotifications();
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
        this.displayAssessorStats = CompetitionCompletionStage.COMPETITION_CLOSE.equals(completionStage);
    }

    public CompetitionInFlightStatsViewModel(CompetitionOpenKeyApplicationStatisticsResource
                                                     competitionOpenKeyApplicationStatisticsResource,
                                             CompetitionOpenKeyAssessmentStatisticsResource
                                                     competitionOpenKeyAssessmentStatisticsResource,
                                             CompetitionStatus competitionStatus,
                                             CompetitionCompletionStage completionStage) {
        this.statOne = competitionOpenKeyAssessmentStatisticsResource.getAssessorsInvited();
        this.statTwo = competitionOpenKeyAssessmentStatisticsResource.getAssessorsAccepted();
        this.statThree = competitionOpenKeyApplicationStatisticsResource.getApplicationsPerAssessor();
        this.statFour = competitionOpenKeyApplicationStatisticsResource.getApplicationsStarted();
        this.statFive = competitionOpenKeyApplicationStatisticsResource.getApplicationsPastHalf();
        this.statSix = competitionOpenKeyApplicationStatisticsResource.getApplicationsSubmitted();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
        this.displayAssessorStats = CompetitionCompletionStage.COMPETITION_CLOSE.equals(completionStage);
    }

    public CompetitionInFlightStatsViewModel(CompetitionClosedKeyApplicationStatisticsResource
                                                     competitionClosedKeyApplicationStatisticsResource,
                                             CompetitionClosedKeyAssessmentStatisticsResource
                                                     competitionClosedKeyAssessmentStatisticsResource,
                                             CompetitionStatus competitionStatus,
                                             CompetitionCompletionStage completionStage) {
        this.statOne = competitionClosedKeyApplicationStatisticsResource.getApplicationsRequiringAssessors();
        this.statTwo = competitionClosedKeyApplicationStatisticsResource.getAssignmentCount();
        this.statThree = competitionClosedKeyAssessmentStatisticsResource.getAssessorsWithoutApplications();
        this.statFour = competitionClosedKeyAssessmentStatisticsResource.getAssessorsInvited();
        this.statFive = competitionClosedKeyAssessmentStatisticsResource.getAssessorsAccepted();
        this.statSix = competitionClosedKeyApplicationStatisticsResource.getApplicationsPerAssessor();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
        this.displayAssessorStats = CompetitionCompletionStage.COMPETITION_CLOSE.equals(completionStage);
    }

    public CompetitionInFlightStatsViewModel(CompetitionInAssessmentKeyAssessmentStatisticsResource
                                                     keyStatisticsResource,
                                             CompetitionStatus competitionStatus,
                                             CompetitionCompletionStage completionStage) {
        this.statOne = keyStatisticsResource.getAssignmentCount();
        this.statTwo = keyStatisticsResource.getAssignmentsWaiting();
        this.statThree = keyStatisticsResource.getAssignmentsAccepted();
        this.statFour = keyStatisticsResource.getAssessmentsStarted();
        this.statFive = keyStatisticsResource.getAssessmentsSubmitted();
        this.statSix = keyStatisticsResource.getSupportersInvited();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
        this.displayAssessorStats = CompetitionCompletionStage.COMPETITION_CLOSE.equals(completionStage);
    }

    public CompetitionInFlightStatsViewModel(CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource,
                                             CompetitionStatus competitionStatus,
                                             CompetitionCompletionStage completionStage) {
        this.statOne = keyStatisticsResource.getApplicationsSubmitted();
        this.statTwo = keyStatisticsResource.getApplicationsFunded();
        this.statThree = keyStatisticsResource.getApplicationsNotFunded();
        this.statFour = keyStatisticsResource.getApplicationsOnHold();
        this.statFive = keyStatisticsResource.getApplicationsNotifiedOfDecision();
        this.statSix = keyStatisticsResource.getApplicationsAwaitingDecision();
        this.canManageFundingNotifications = keyStatisticsResource.isCanManageFundingNotifications();
        this.canReleaseFeedback = keyStatisticsResource.isCanReleaseFeedback();
        this.status = competitionStatus;
        this.displayAssessorStats = CompetitionCompletionStage.COMPETITION_CLOSE.equals(completionStage);
    }

    public CompetitionInFlightStatsViewModel(CompetitionEoiKeyApplicationStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getEOISubmitted();
        this.statTwo = keyStatisticsResource.getEOISuccessful();
        this.statThree = keyStatisticsResource.getEOIUnsuccessful();
        this.statFour = keyStatisticsResource.getEOINotifiedOfDecision();
        this.statFive = keyStatisticsResource.getEOIAwaitingDecision();
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

    public boolean isDisplayAssessorStats() {
        return displayAssessorStats;
    }
}