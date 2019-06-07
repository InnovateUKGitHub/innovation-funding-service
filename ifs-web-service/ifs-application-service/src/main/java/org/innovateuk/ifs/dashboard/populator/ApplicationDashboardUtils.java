package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationForEuGrantTransferResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInSetupResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.dashboard.viewmodel.EuGrantTransferDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.InProgressDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.InSetupDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.PreviousDashboardRowViewModel;

import java.util.function.Function;

class ApplicationDashboardUtils {

    private ApplicationDashboardUtils() {}

    static Function<DashboardApplicationInSetupResource, InSetupDashboardRowViewModel> toInSetupViewModel() {
        return resource -> new InSetupDashboardRowViewModel(
                resource.getTitle(),
                resource.getApplicationId(),
                resource.getCompetitionTitle(),
                resource.getProjectId(),
                resource.getProjectTitle(),
                resource.getTargetStartDate());
    }

    static Function<DashboardApplicationForEuGrantTransferResource, EuGrantTransferDashboardRowViewModel> toEuGrantTransferViewModel() {
        return resource -> new EuGrantTransferDashboardRowViewModel(
                resource.getTitle(),
                resource.getApplicationId(),
                resource.getCompetitionTitle(),
                resource.getApplicationState(),
                resource.getApplicationProgress(),
                resource.getProjectId(),
                resource.getStartDate());
    }

    static Function<DashboardApplicationInProgressResource, InProgressDashboardRowViewModel> toInProgressViewModel() {
        return resource -> new InProgressDashboardRowViewModel(
                resource.getTitle(),
                resource.getApplicationId(),
                resource.getCompetitionTitle(),
                resource.isAssignedToMe(),
                resource.getApplicationState(),
                resource.isLeadApplicant(),
                resource.getEndDate(),
                resource.getDaysLeft(),
                resource.getApplicationProgress(),
                resource.isAssignedToInterview(),
                resource.getStartDate());
    }

    static Function<DashboardPreviousApplicationResource, PreviousDashboardRowViewModel> toPreviousViewModel() {
        return resource -> new PreviousDashboardRowViewModel(
                resource.getTitle(),
                resource.getApplicationId(),
                resource.getCompetitionTitle(),
                resource.getApplicationState(),
                resource.getStartDate());
    }
}
