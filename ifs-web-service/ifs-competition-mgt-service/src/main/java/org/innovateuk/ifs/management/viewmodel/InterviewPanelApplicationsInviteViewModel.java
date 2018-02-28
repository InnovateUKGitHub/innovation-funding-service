package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the invite applications for Assessment Interview Panel 'Invite' applications view.
 */
public class InterviewPanelApplicationsInviteViewModel extends InterviewPanelApplicationsViewModel<InterviewPanelApplicationInviteRowViewModel> {

    public InterviewPanelApplicationsInviteViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<InterviewPanelApplicationInviteRowViewModel> applications,
            int applicationsInCompetition,
            int applicationsInPanel,
            PaginationViewModel pagination,
            String originQuery) {
        super(competitionId, competitionName, innovationArea, innovationSector, applications, applicationsInCompetition,
                applicationsInPanel, pagination, originQuery);
    }
}