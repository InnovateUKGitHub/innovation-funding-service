package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors for Assessment Interview Panel 'Invite' view.
 */
public class InterviewPanelApplicationsInviteViewModel extends InterviewPanelApplicationsViewModel<InterviewPanelApplicationInviteRowViewModel> {

    public InterviewPanelApplicationsInviteViewModel(
            long competitionId,
            String competitionName,
            String innovationSector,
            String innovationArea,
            List<InterviewPanelApplicationInviteRowViewModel> applications,
            int applicationsInCompetition,
            int applicationsInPanel,
            PaginationViewModel pagination,
            String originQuery) {
        super(competitionId, competitionName, innovationSector, innovationArea, applications, applicationsInCompetition,
                applicationsInPanel, pagination, originQuery);
    }
}