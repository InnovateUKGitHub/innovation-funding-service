package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * Holder of model attributes for the invite applications for Assessment Interview Panel 'view status' applications view.
 */
public class InterviewAssignmentApplicationStatusViewModel  extends InterviewAssignmentApplicationsViewModel<InterviewAssignmentApplicationStatusRowViewModel> {

    public InterviewAssignmentApplicationStatusViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<InterviewAssignmentApplicationStatusRowViewModel> applications,
            InterviewAssignmentKeyStatisticsResource keyStatisticsResource,
            Pagination pagination,
            String originQuery
    ) {
        super(competitionId, competitionName, innovationArea, innovationSector, applications, keyStatisticsResource,
                pagination, originQuery);
    }
}