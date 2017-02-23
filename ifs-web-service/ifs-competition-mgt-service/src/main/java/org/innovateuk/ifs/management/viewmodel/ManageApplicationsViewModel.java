package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class ManageApplicationsViewModel {
    private Long competitionId;
    private String competitionName;
    private List<ManageApplicationsRowViewModel> applications;
    private boolean inAssessment;

    public ManageApplicationsViewModel(Long competitionId,
                                       String competitionName,
                                       List<ManageApplicationsRowViewModel> applications,
                                       boolean inAssessment) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
        this.inAssessment = inAssessment;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ManageApplicationsRowViewModel> getApplications() {
        return applications;
    }

    public boolean getInAssessment() {
        return inAssessment;
    }
}
