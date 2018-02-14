package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors for Assessment Interview Panel 'Invite' view.
 */
public class InterviewPanelInviteApplicationsInviteViewModel {

    private final long competitionId;
    private final String competitionName;
    private final List<InterviewPanelApplicationInviteRowViewModel> applications;
    private final String innovationSector;
    private final String innovationArea;
    private final int applicationsInCompetition;
    private final int applicationsInPanel;
    private final PaginationViewModel pagination;
    private final String originQuery;

    public InterviewPanelInviteApplicationsInviteViewModel(
            long competitionId,
            String competitionName,
            String innovationSector,
            String innovationArea,
            List<InterviewPanelApplicationInviteRowViewModel> applications,
            int applicationsInCompetition,
            int applicationsInPanel,
            PaginationViewModel pagination,
            String originQuery) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
        this.pagination = pagination;
        this.originQuery = originQuery;
        this.innovationSector = innovationSector;
        this.innovationArea = innovationArea;
        this.applicationsInCompetition = applicationsInCompetition;
        this.applicationsInPanel = applicationsInPanel;
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

    public List<InterviewPanelApplicationInviteRowViewModel> getApplications() {
        return applications;
    }

    public String getInnovationSector() {
        return innovationSector;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public int getApplicationsInCompetition() {
        return applicationsInCompetition;
    }

    public int getApplicationsInPanel() {
        return applicationsInPanel;
    }
}