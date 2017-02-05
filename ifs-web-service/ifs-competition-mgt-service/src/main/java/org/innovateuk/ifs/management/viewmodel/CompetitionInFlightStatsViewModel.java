package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionReadyToOpenKeyStatisticsResource;

/**
 * Holder of key statistics for the in-flight dashboard
 */
public class CompetitionInFlightStatsViewModel {

    private long statOne;
    private long statTwo;
    private Long statThree;
    private Long statFour;
    private Long statFive;
    private Long statSix;

    public CompetitionInFlightStatsViewModel() {

    }

    public CompetitionInFlightStatsViewModel(CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getAssessorsInvited();
        this.statTwo = keyStatisticsResource.getAssessorsAccepted();
    }

    public CompetitionInFlightStatsViewModel(CompetitionOpenKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getAssessorsInvited();
        this.statTwo = keyStatisticsResource.getAssessorsAccepted();
        this.statThree = keyStatisticsResource.getApplicationsPerAssessor();
        this.statFour = keyStatisticsResource.getApplicationsStarted();
        this.statFive = keyStatisticsResource.getApplicationsPastHalf();
        this.statSix = keyStatisticsResource.getApplicationsSubmitted();
    }

    public CompetitionInFlightStatsViewModel(CompetitionClosedKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getApplicationsRequiringAssessors();
        this.statTwo = keyStatisticsResource.getAssignmentCount();
        this.statThree = keyStatisticsResource.getAssessorsWithoutApplications();
        this.statFour = keyStatisticsResource.getAssessorsInvited();
        this.statFive = keyStatisticsResource.getAssessorsAccepted();
        this.statSix = keyStatisticsResource.getApplicationsPerAssessor();
    }

    public CompetitionInFlightStatsViewModel(CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getAssignmentCount();
        this.statTwo = keyStatisticsResource.getAssignmentsWaiting();
        this.statThree = keyStatisticsResource.getAssignmentsAccepted();
        this.statFour = keyStatisticsResource.getAssessmentsStarted();
        this.statFive = keyStatisticsResource.getAssessmentsSubmitted();
    }

    public long getStatOne() {
        return statOne;
    }

    public void setStatOne(long statOne) {
        this.statOne = statOne;
    }

    public long getStatTwo() {
        return statTwo;
    }

    public void setStatTwo(long statTwo) {
        this.statTwo = statTwo;
    }

    public Long getStatThree() {
        return statThree;
    }

    public void setStatThree(Long statThree) {
        this.statThree = statThree;
    }

    public Long getStatFour() {
        return statFour;
    }

    public void setStatFour(Long statFour) {
        this.statFour = statFour;
    }

    public Long getStatFive() {
        return statFive;
    }

    public void setStatFive(Long statFive) {
        this.statFive = statFive;
    }

    public Long getStatSix() {
        return statSix;
    }

    public void setStatSix(Long statSix) {
        this.statSix = statSix;
    }
}
