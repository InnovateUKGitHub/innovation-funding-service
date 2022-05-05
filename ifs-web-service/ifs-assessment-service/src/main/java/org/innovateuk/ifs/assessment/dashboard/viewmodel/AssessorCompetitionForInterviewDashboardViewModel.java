package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import lombok.Getter;

import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition for interview Dashboard.
 */
@Getter
public class AssessorCompetitionForInterviewDashboardViewModel {

    private long competitionId;
    private String competitionTitle;
    private String leadTechnologist;
    private List<AssessorCompetitionForInterviewDashboardApplicationViewModel> applications;
    private String hash;

    public AssessorCompetitionForInterviewDashboardViewModel(long competitionId,
                                                             String competitionTitle,
                                                             String leadTechnologist,
                                                             List<AssessorCompetitionForInterviewDashboardApplicationViewModel> applications,
                                                             String hash) {
        this.competitionId = competitionId;
        this.competitionTitle = competitionTitle;
        this.leadTechnologist = leadTechnologist;
        this.applications = applications;
        this.hash = hash;
    }

}
