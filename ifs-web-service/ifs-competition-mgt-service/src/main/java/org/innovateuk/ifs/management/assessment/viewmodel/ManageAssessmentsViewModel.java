package org.innovateuk.ifs.management.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.pagination.PaginationViewModel;

import java.util.List;

/**
 * View model for the 'Manage assessments' dashboard page.
 *
 * Contains statistics and links to other pages such as 'Manage applications' and 'Manage assessors'.
 */
public class ManageAssessmentsViewModel {

    private final long competitionId;
    private final String competitionName;
    private final boolean inAssessment;

    private final int totalAssessments;
    private final int assessmentsAwaitingResponse;
    private final int assessmentsAccepted;
    private final int assessmentsStarted;
    private final int assessmentsCompleted;
    private final boolean alwaysOpen;
    private final List<AssessmentPeriodForm> assessmentPeriods;
    private final PaginationViewModel assessmentPeriodPagination;

    public ManageAssessmentsViewModel(CompetitionResource competition,
                                      CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatistics,
                                      List<AssessmentPeriodForm> assessmentPeriods,
                                      PaginationViewModel assessmentPeriodPagination) {
        this.competitionId = competition.getId();
        this.competitionName = competition.getName();
        this.inAssessment = competition.getCompetitionStatus() == CompetitionStatus.IN_ASSESSMENT;

        this.totalAssessments = keyStatistics.getAssignmentCount();
        this.assessmentsAwaitingResponse = keyStatistics.getAssignmentsWaiting();
        this.assessmentsAccepted = keyStatistics.getAssignmentsAccepted();
        this.assessmentsStarted = keyStatistics.getAssessmentsStarted();
        this.assessmentsCompleted = keyStatistics.getAssessmentsSubmitted();
        this.alwaysOpen = competition.isAlwaysOpen();
        this.assessmentPeriods = assessmentPeriods;
        this.assessmentPeriodPagination = assessmentPeriodPagination;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isInAssessment() {
        return inAssessment;
    }

    public int getTotalAssessments() {
        return totalAssessments;
    }

    public int getAssessmentsAwaitingResponse() {
        return assessmentsAwaitingResponse;
    }

    public int getAssessmentsAccepted() {
        return assessmentsAccepted;
    }

    public int getAssessmentsCompleted() {
        return assessmentsCompleted;
    }

    public int getAssessmentsStarted() {
        return assessmentsStarted;
    }

    public boolean isAlwaysOpen() {
        return alwaysOpen;
    }

    public List<AssessmentPeriodForm> getAssessmentPeriods() {
        return assessmentPeriods;
    }

    public PaginationViewModel getAssessmentPeriodPagination() {
        return assessmentPeriodPagination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ManageAssessmentsViewModel that = (ManageAssessmentsViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(inAssessment, that.inAssessment)
                .append(totalAssessments, that.totalAssessments)
                .append(assessmentsAwaitingResponse, that.assessmentsAwaitingResponse)
                .append(assessmentsAccepted, that.assessmentsAccepted)
                .append(assessmentsStarted, that.assessmentsStarted)
                .append(assessmentsCompleted, that.assessmentsCompleted)
                .append(alwaysOpen, that.alwaysOpen)
                .append(competitionName, that.competitionName)
                .append(assessmentPeriods, that.assessmentPeriods)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(inAssessment)
                .append(totalAssessments)
                .append(assessmentsAwaitingResponse)
                .append(assessmentsAccepted)
                .append(assessmentsStarted)
                .append(assessmentsCompleted)
                .append(alwaysOpen)
                .append(assessmentPeriods)
                .toHashCode();
    }
}
