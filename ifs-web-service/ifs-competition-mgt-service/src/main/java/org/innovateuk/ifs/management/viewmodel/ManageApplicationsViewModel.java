package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class ManageApplicationsViewModel {
    private Long competitionId;
    private String competitionName;
    private List<ManageApplicationsRowViewModel> applications;

    public ManageApplicationsViewModel(Long competitionId, String competitionName, List<ManageApplicationsRowViewModel> applications) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
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
}
