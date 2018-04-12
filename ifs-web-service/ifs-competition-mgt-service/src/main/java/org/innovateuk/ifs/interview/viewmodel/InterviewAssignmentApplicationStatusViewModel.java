package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;

import java.util.List;
import java.util.Objects;

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
            int applicationsInCompetition,
            int applicationsInPanel,
            PaginationViewModel pagination,
            String originQuery
    ) {
        super(competitionId, competitionName, innovationArea, innovationSector, applications, applicationsInCompetition,
                applicationsInPanel, pagination, originQuery);
    }
}