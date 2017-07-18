package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

public class AssessorAssessmentProgressViewModel {

    private final long competitionId;
    private final String competitionName;
    private final String assessorName;
    private final List<String> innovationAreas;
    private final String businessType;
    private final long totalApplications;

    private final List<AssessorAssessmentProgressAssignedRowViewModel> assigned;
    private final AssessorAssessmentProgressApplicationsViewModel applicationsView;

    public AssessorAssessmentProgressViewModel(long competitionId,
                                               String competitionName,
                                               String assessorName,
                                               List<String> innovationAreas,
                                               String businessType,
                                               long totalApplications,
                                               List<AssessorAssessmentProgressAssignedRowViewModel> assigned,
                                               AssessorAssessmentProgressApplicationsViewModel applicationsView) {

        this.competitionId = competitionId;
        this.competitionName = competitionName;
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
}
