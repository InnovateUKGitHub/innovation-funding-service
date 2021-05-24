package org.innovateuk.ifs.management.assessment.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class AssessorAssessmentProgressViewModel {

    private static final Set<CompetitionStatus> IN_ASSESSMENT_STATES = EnumSet.complementOf(EnumSet.of(
            CompetitionStatus.COMPETITION_SETUP,
            CompetitionStatus.READY_TO_OPEN,
            CompetitionStatus.OPEN,
            CompetitionStatus.CLOSED
    ));

    private final long competitionId;
    private final String competitionName;
    private final CompetitionStatus competitionStatus;
    private final boolean competitionAlwaysOpen;
    private final String assessorName;
    private final List<String> innovationAreas;
    private final String businessType;
    private final long totalApplications;
    private final long assessorId;
    private final long assessmentPeriodId;
    private final String assessmentPeriodName;
    private final String filter;
    private final boolean selectAllDisabled;
    private final boolean isSuperAdmin;

    private final List<AssessorAssessmentProgressAssignedRowViewModel> assigned;
    private final List<AssessorAssessmentProgressRejectedRowViewModel> rejected;
    private final List<AssessorAssessmentProgressWithdrawnRowViewModel> previouslyAssigned;
    private final AssessorAssessmentProgressApplicationsViewModel applicationsView;

    public AssessorAssessmentProgressViewModel(long competitionId,
                                               String competitionName,
                                               CompetitionStatus competitionStatus,
                                               boolean competitionAlwaysOpen,
                                               long assessorId,
                                               long assessmentPeriodId,
                                               String assessmentPeriodName,
                                               String assessorName,
                                               List<String> innovationAreas,
                                               String filter,
                                               String businessType,
                                               long totalApplications,
                                               boolean selectAllDisabled,
                                               List<AssessorAssessmentProgressAssignedRowViewModel> assigned,
                                               List<AssessorAssessmentProgressRejectedRowViewModel> rejected,
                                               List<AssessorAssessmentProgressWithdrawnRowViewModel> previouslyAssigned,
                                               AssessorAssessmentProgressApplicationsViewModel applicationsView,
                                               boolean isSuperAdmin) {

        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
        this.competitionAlwaysOpen = competitionAlwaysOpen;
        this.assessorName = assessorName;
        this.innovationAreas = innovationAreas;
        this.businessType = businessType;
        this.totalApplications = totalApplications;
        this.assessorId = assessorId;
        this.assessmentPeriodId = assessmentPeriodId;
        this.assessmentPeriodName = assessmentPeriodName;
        this.assigned = assigned;
        this.rejected = rejected;
        this.filter = filter;
        this.selectAllDisabled = selectAllDisabled;
        this.previouslyAssigned = previouslyAssigned;
        this.applicationsView = applicationsView;
        this.isSuperAdmin = isSuperAdmin;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public String getAssessorName() {
        return assessorName;
    }

    public List<String> getInnovationAreas() {
        return innovationAreas;
    }

    public String getBusinessType() {
        return businessType;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public long getAssessorId() { return assessorId; }

    public long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public String getAssessmentPeriodName() {
        return assessmentPeriodName;
    }

    public boolean isOnlyAssessmentPeriod() {
        return assessmentPeriodName == null;
    }

    public List<AssessorAssessmentProgressAssignedRowViewModel> getAssigned() {
        return assigned;
    }

    public List<AssessorAssessmentProgressRejectedRowViewModel> getRejected() {
        return rejected;
    }

    public List<AssessorAssessmentProgressWithdrawnRowViewModel> getPreviouslyAssigned() {
        return previouslyAssigned;
    }

    public String getFilter() {return filter; }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public AssessorAssessmentProgressApplicationsViewModel getApplicationsView() {
        return applicationsView;
    }

    public boolean isCompetitionInAssessment() {
        return IN_ASSESSMENT_STATES.contains(competitionStatus);
    }

    public boolean isCompetitionAlwaysOpen() {
        return competitionAlwaysOpen;
    }
    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public boolean isAssessmentClosed() {
        return competitionStatus.isLaterThan(CompetitionStatus.IN_ASSESSMENT);
    }
}
