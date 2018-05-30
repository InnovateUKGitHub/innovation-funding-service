package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.management.core.viewmodel.PaginationViewModel;

import java.util.List;

/**
 * Holder of model attributes for the invite applications for Assessment Interview Panel 'Find' applications view.
 */
public class InterviewAssignmentApplicationsFindViewModel extends InterviewAssignmentApplicationsViewModel<InterviewAssignmentApplicationRowViewModel> {

    private final boolean selectAllDisabled;

    public InterviewAssignmentApplicationsFindViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<InterviewAssignmentApplicationRowViewModel> applications,
            InterviewAssignmentKeyStatisticsResource keyStatisticsResource,
            PaginationViewModel pagination,
            String originQuery,
            boolean selectAllDisabled) {
        super(competitionId, competitionName, innovationArea, innovationSector, applications, keyStatisticsResource,
                pagination, originQuery);
        this.selectAllDisabled = selectAllDisabled;
    }

    public boolean isSelectAllDisabled() {
        return selectAllDisabled;
    }
}