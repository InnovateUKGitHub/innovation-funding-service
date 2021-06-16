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
    private String assessmentPeriodName;
    private Pagination pagination;
    private Long assessmentPeriodId;
    private boolean alwaysOpen;

    public ManageApplicationsViewModel(Long competitionId,
                                       String competitionName,
                                       Long assessmentPeriodId,
                                       String assessmentPeriodName,
                                       List<ManageApplicationsRowViewModel> applications,
                                       boolean inAssessment,
                                       boolean alwaysOpen,
                                       String filter,
                                       Pagination pagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.assessmentPeriodId = assessmentPeriodId;
        this.assessmentPeriodName = assessmentPeriodName;
        this.applications = applications;
        this.inAssessment = inAssessment;
        this.filter = filter;
        this.pagination = pagination;
        this.alwaysOpen = alwaysOpen;
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

    public String getAssessmentPeriodName() {
        return assessmentPeriodName;
    }

    public Long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public boolean isAlwaysOpen() {
        return alwaysOpen;
    }
}
