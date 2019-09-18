package org.innovateuk.ifs.management.interview.viewmodel;

import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.management.navigation.Pagination;

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
            Pagination pagination) {
        super(competitionId, competitionName, innovationArea, innovationSector, applications, keyStatisticsResource,
                pagination);
    }
}