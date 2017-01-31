package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class ManageApplicationsViewModel {
    private Long competitionId;
    private String competitionName;
    private List<ManageApplicationsRowViewModel> applications;
    private CompetitionStatus competitionStatus;

    public ManageApplicationsViewModel(Long competitionId, String competitionName, List<ManageApplicationsRowViewModel> applications, CompetitionStatus competitionStatus) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
        this.competitionStatus = competitionStatus;
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

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }
}
