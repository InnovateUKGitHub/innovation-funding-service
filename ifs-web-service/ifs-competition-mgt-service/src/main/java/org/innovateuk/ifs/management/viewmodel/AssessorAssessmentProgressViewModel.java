package org.innovateuk.ifs.management.viewmodel;

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
    private final String assessorName;
    private final List<String> innovationAreas;
    private final String businessType;
    private final long totalApplications;

    private final List<AssessorAssessmentProgressAssignedRowViewModel> assigned;
    private final AssessorAssessmentProgressApplicationsViewModel applicationsView;

    public AssessorAssessmentProgressViewModel(long competitionId,
                                               String competitionName,
                                               CompetitionStatus competitionStatus,
                                               String assessorName,
                                               List<String> innovationAreas,
                                               String businessType,
                                               long totalApplications,
                                               List<AssessorAssessmentProgressAssignedRowViewModel> assigned,
                                               AssessorAssessmentProgressApplicationsViewModel applicationsView) {

        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
        this.assessorName = assessorName;
        this.innovationAreas = innovationAreas;
        this.businessType = businessType;
        this.totalApplications = totalApplications;
        this.assigned = assigned;
        this.applicationsView = applicationsView;
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

    public List<AssessorAssessmentProgressAssignedRowViewModel> getAssigned() {
        return assigned;
    }

    public AssessorAssessmentProgressApplicationsViewModel getApplicationsView() {
        return applicationsView;
    }

    public boolean isCompetitionInAssessment() {
        return IN_ASSESSMENT_STATES.contains(competitionStatus);
    }

}
