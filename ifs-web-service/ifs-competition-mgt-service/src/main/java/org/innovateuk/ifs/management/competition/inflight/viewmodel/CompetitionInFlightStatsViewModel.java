package org.innovateuk.ifs.management.competition.inflight.viewmodel;

import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

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

    public CompetitionInFlightStatsViewModel(CompetitionReadyToOpenKeyAssessmentStatisticsResource
                                                     keyStatisticsResource, CompetitionStatus competitionStatus) {
        this.statOne = keyStatisticsResource.getAssessorsInvited();
        this.statTwo = keyStatisticsResource.getAssessorsAccepted();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionOpenKeyApplicationStatisticsResource
                                                     competitionOpenKeyApplicationStatisticsResource,
                                             CompetitionOpenKeyAssessmentStatisticsResource
                                                     competitionOpenKeyAssessmentStatisticsResource,
                                             CompetitionStatus competitionStatus,
                                             CompetitionFundedKeyApplicationStatisticsResource fundedKeyApplicationStatisticsResource) {
        this.statOne = competitionOpenKeyAssessmentStatisticsResource.getAssessorsInvited();
        this.statTwo = competitionOpenKeyAssessmentStatisticsResource.getAssessorsAccepted();
        this.statThree = competitionOpenKeyApplicationStatisticsResource.getApplicationsPerAssessor();
        this.statFour = competitionOpenKeyApplicationStatisticsResource.getApplicationsStarted();
        this.statFive = competitionOpenKeyApplicationStatisticsResource.getApplicationsPastHalf();
        this.statSix = competitionOpenKeyApplicationStatisticsResource.getApplicationsSubmitted();
        this.canManageFundingNotifications = fundedKeyApplicationStatisticsResource.isCanManageFundingNotifications();
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionOpenKeyApplicationStatisticsResource
                                                     competitionOpenKeyApplicationStatisticsResource,
                                             CompetitionOpenKeyAssessmentStatisticsResource
                                                     competitionOpenKeyAssessmentStatisticsResource,
                                             CompetitionStatus competitionStatus) {
        this.statOne = competitionOpenKeyAssessmentStatisticsResource.getAssessorsInvited();
        this.statTwo = competitionOpenKeyAssessmentStatisticsResource.getAssessorsAccepted();
        this.statThree = competitionOpenKeyApplicationStatisticsResource.getApplicationsPerAssessor();
        this.statFour = competitionOpenKeyApplicationStatisticsResource.getApplicationsStarted();
        this.statFive = competitionOpenKeyApplicationStatisticsResource.getApplicationsPastHalf();
        this.statSix = competitionOpenKeyApplicationStatisticsResource.getApplicationsSubmitted();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionClosedKeyApplicationStatisticsResource
                                                     competitionClosedKeyApplicationStatisticsResource,
                                             CompetitionClosedKeyAssessmentStatisticsResource
                                                     competitionClosedKeyAssessmentStatisticsResource,
                                             CompetitionStatus competitionStatus) {
        this.statOne = competitionClosedKeyApplicationStatisticsResource.getApplicationsRequiringAssessors();
        this.statTwo = competitionClosedKeyApplicationStatisticsResource.getAssignmentCount();
        this.statThree = competitionClosedKeyAssessmentStatisticsResource.getAssessorsWithoutApplications();
        this.statFour = competitionClosedKeyAssessmentStatisticsResource.getAssessorsInvited();
        this.statFive = competitionClosedKeyAssessmentStatisticsResource.getAssessorsAccepted();
        this.statSix = competitionClosedKeyApplicationStatisticsResource.getApplicationsPerAssessor();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionInAssessmentKeyAssessmentStatisticsResource
                                                     keyStatisticsResource,
                                             CompetitionStatus competitionStatus) {
        this.statOne = keyStatisticsResource.getAssignmentCount();
        this.statTwo = keyStatisticsResource.getAssignmentsWaiting();
        this.statThree = keyStatisticsResource.getAssignmentsAccepted();
        this.statFour = keyStatisticsResource.getAssessmentsStarted();
        this.statFive = keyStatisticsResource.getAssessmentsSubmitted();
        this.canManageFundingNotifications = false;
        this.status = competitionStatus;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource,
                                             CompetitionStatus competitionStatus) {
        this.statOne = keyStatisticsResource.getApplicationsSubmitted();
        this.statTwo = keyStatisticsResource.getApplicationsFunded();
        this.statThree = keyStatisticsResource.getApplicationsNotFunded();
        this.statFour = keyStatisticsResource.getApplicationsOnHold();
        this.statFive = keyStatisticsResource.getApplicationsNotifiedOfDecision();
        this.statSix = keyStatisticsResource.getApplicationsAwaitingDecision();
        this.canManageFundingNotifications = keyStatisticsResource.isCanManageFundingNotifications();
        this.canReleaseFeedback = keyStatisticsResource.isCanReleaseFeedback();
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