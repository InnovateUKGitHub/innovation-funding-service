package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the invite applications for Assessment Interview Panel 'Find' applications view.
 */
public class InterviewPanelApplicationsFindViewModel extends InterviewPanelApplicationsViewModel<InterviewPanelApplicationRowViewModel> {

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
        super(competitionId, competitionName, innovationArea, innovationSector, applications,
                applicationsInCompetition, applicationsInPanel, pagination, originQuery);
        this.selectAllDisabled = selectAllDisabled;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }
}