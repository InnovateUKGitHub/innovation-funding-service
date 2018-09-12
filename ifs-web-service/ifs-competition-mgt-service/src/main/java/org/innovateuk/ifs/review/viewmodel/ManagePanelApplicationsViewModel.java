package org.innovateuk.ifs.review.viewmodel;


import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * Holder of model attributes for the Manage applications page
 */
public class ManagePanelApplicationsViewModel {
    private long competitionId;
    private String competitionName;
    private String competitionStatus;
    private List<ManageReviewApplicationsRowViewModel> applications;
    private String filter;
    private String sorting;
    private Pagination pagination;
    private List<ManageReviewApplicationsRowViewModel> assignedApplications;

    public ManagePanelApplicationsViewModel(Long competitionId,
                                            String competitionName,
                                            String competitionStatus,
                                            List<ManageReviewApplicationsRowViewModel> applications,
                                            List<ManageReviewApplicationsRowViewModel> assignedApplications,
                                            String filter,
                                            String sorting,
                                            Pagination pagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
        this.applications = applications;
        this.assignedApplications = assignedApplications;
        this.filter = filter;
        this.sorting = sorting;
        this.pagination = pagination;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getCompetitionStatus() { return competitionStatus; }

    public List<ManageReviewApplicationsRowViewModel> getApplications() {
        return applications;
    }

    public List<ManageReviewApplicationsRowViewModel> getAssignedApplications() {
        return assignedApplications;
    }

    public String getFilter() {
        return filter;
    }

    public String getSorting() { return sorting; }

    public Pagination getPagination() {
        return pagination;
    }
}

