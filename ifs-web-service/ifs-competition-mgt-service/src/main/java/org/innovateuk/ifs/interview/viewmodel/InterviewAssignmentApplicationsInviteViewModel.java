package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;

import java.util.List;

/**
 * Holder of model attributes for the Invite assessors for Assessment Interview Panel 'Invite' view.
 */
public class InterviewAssignmentApplicationsInviteViewModel extends InterviewAssignmentApplicationsViewModel<InterviewAssignmentApplicationInviteRowViewModel> {

    public InterviewAssignmentApplicationsInviteViewModel(
            long competitionId,
            String competitionName,
            String innovationSector,
            String innovationArea,
            List<InterviewAssignmentApplicationInviteRowViewModel> applications,
            int applicationsInCompetition,
            int applicationsInPanel,
            PaginationViewModel pagination,
            String originQuery) {
        super(competitionId, competitionName, innovationSector, innovationArea, applications, applicationsInCompetition,
                applicationsInPanel, pagination, originQuery);
    }
}