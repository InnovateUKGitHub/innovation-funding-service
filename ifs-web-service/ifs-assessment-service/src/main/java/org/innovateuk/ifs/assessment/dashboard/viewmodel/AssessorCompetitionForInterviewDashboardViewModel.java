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
    private final String origin;

    public AssessorCompetitionForInterviewDashboardViewModel(long competitionId,
                                                             String competitionTitle,
                                                             String leadTechnologist,
                                                             List<AssessorCompetitionForInterviewDashboardApplicationViewModel> applications,
                                                             String origin) {
        this.competitionId = competitionId;
        this.competitionTitle = competitionTitle;
        this.leadTechnologist = leadTechnologist;
        this.applications = applications;
        this.origin = origin;
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

    public String getOrigin() {
        return origin;
    }
}
