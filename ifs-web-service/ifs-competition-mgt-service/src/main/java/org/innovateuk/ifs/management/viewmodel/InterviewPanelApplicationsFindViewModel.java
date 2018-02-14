package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors for Assessment Interview Panel 'Find' view.
 */
public class InterviewPanelApplicationsFindViewModel {

    private final long competitionId;
    private final String competitionName;
    private final List<InterviewPanelApplicationRowViewModel> applications;
    private final String innovationSector;
    private final String innovationArea;
    private final PaginationViewModel pagination;
    private final String originQuery;
    private final int applicationsInCompetition;
    private final int applicationsInPanel;
    private final boolean selectAllDisabled;

    public InterviewPanelApplicationsFindViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<InterviewPanelApplicationRowViewModel> applications,
            int applicationsInCompetition,
            int applicationsInPanel,
            PaginationViewModel pagination,
            String originQuery,
            boolean selectAllDisabled) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.innovationArea = innovationArea;
        this.innovationSector = innovationSector;
        this.applications = applications;
        this.pagination = pagination;
        this.originQuery = originQuery;
        this.selectAllDisabled = selectAllDisabled;
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

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }

    public List<InterviewPanelApplicationRowViewModel> getApplications() {
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