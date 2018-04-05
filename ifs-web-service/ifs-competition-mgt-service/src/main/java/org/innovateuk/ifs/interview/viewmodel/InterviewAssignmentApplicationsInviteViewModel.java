package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;

import java.util.List;

/**
 * Holder of model attributes for the invite applications for Assessment Interview Panel 'Invite' applications view.
 */
public class InterviewAssignmentApplicationsInviteViewModel extends InterviewAssignmentApplicationsViewModel<InterviewAssignmentApplicationInviteRowViewModel> {

    public InterviewAssignmentApplicationsInviteViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<InterviewAssignmentApplicationInviteRowViewModel> applications,
            InterviewAssignmentKeyStatisticsResource keyStatisticsResource,
            PaginationViewModel pagination,
            String originQuery) {
        super(competitionId, competitionName, innovationArea, innovationSector, applications, keyStatisticsResource,
                pagination, originQuery);
    }
}