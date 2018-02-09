package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors for Assessment Interview Panel 'Find' view.
 */
public class InterviewPanelInviteApplicationsFindViewModel {

    private final long competitionId;
    private final String competitionName;
    private final List<InterviewPanelApplicationRowViewModel> applications;

    private final PaginationViewModel pagination;
    private final String originQuery;

    private final boolean selectAllDisabled;

    public InterviewPanelInviteApplicationsFindViewModel(
            long competitionId,
            String competitionName,
            List<InterviewPanelApplicationRowViewModel> applications,
            PaginationViewModel pagination,
            String originQuery,
            boolean selectAllDisabled) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
        this.pagination = pagination;
        this.originQuery = originQuery;
        this.selectAllDisabled = selectAllDisabled;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public List<InterviewPanelApplicationRowViewModel> getApplications() {
        return applications;
    }
}