package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition for interview Dashboard.
 */
public class AssessorCompetitionForInterviewDashboardViewModel {

    private long competitionId;
    private String competitionTitle;
    private String leadTechnologist;
    private List<AssessorCompetitionForInterviewDashboardApplicationViewModel> applications;
    private final String originQuery;

    public AssessorCompetitionForInterviewDashboardViewModel(long competitionId,
                                                             String competitionTitle,
                                                             String leadTechnologist,
                                                             List<AssessorCompetitionForInterviewDashboardApplicationViewModel> applications,
                                                             String originQuery) {
        this.competitionId = competitionId;
        this.competitionTitle = competitionTitle;
        this.leadTechnologist = leadTechnologist;
        this.applications = applications;
        this.originQuery = originQuery;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public String getLeadTechnologist() {
        return leadTechnologist;
    }

    public List<AssessorCompetitionForInterviewDashboardApplicationViewModel> getApplications() {
        return applications;
    }

    public String getOriginQuery() {
        return originQuery;
    }
}
