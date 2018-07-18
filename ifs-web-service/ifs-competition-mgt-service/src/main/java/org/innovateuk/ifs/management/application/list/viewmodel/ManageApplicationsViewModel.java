package org.innovateuk.ifs.management.application.list.viewmodel;

import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class ManageApplicationsViewModel {
    private Long competitionId;
    private String competitionName;
    private List<ManageApplicationsRowViewModel> applications;
    private boolean inAssessment;
    private String filter;
    private Pagination pagination;

    public ManageApplicationsViewModel(Long competitionId,
                                       String competitionName,
                                       List<ManageApplicationsRowViewModel> applications,
                                       boolean inAssessment,
                                       String filter,
                                       Pagination pagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
        this.inAssessment = inAssessment;
        this.filter = filter;
        this.pagination = pagination;
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

    public String getFilter() {
        return filter;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
