package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationForEuGrantTransferResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInSetupResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.EuGrantTransferDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.InProgressDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.InSetupDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.PreviousDashboardRowViewModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Populator for the applicant dashboard, it populates an {@link org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel}
 */
@Service
public class ApplicantDashboardPopulator {

    private ApplicantRestService applicantRestService;

    public ApplicantDashboardPopulator(ApplicantRestService applicantRestService) {
        this.applicantRestService = applicantRestService;
    }

    public ApplicantDashboardViewModel populate(Long userId, String originQuery) {
        ApplicantDashboardResource applicantDashboardResource = applicantRestService.getApplicantDashboard(userId);
        return getApplicantDashboardViewModel(originQuery, applicantDashboardResource);
    }

    private ApplicantDashboardViewModel getApplicantDashboardViewModel(String originQuery, ApplicantDashboardResource applicantDashboardResource) {
        List<InSetupDashboardRowViewModel> applicationsInSetUp = toViewModelForInSetup(applicantDashboardResource.getInSetup());
        List<EuGrantTransferDashboardRowViewModel> applicationsForEuGrantTransfers = toViewModelForEuGrantTransfers(applicantDashboardResource.getEuGrantTransfer());
        List<InProgressDashboardRowViewModel> applicationsInProgress = toViewModelForInProgress(applicantDashboardResource.getInProgress());
        List<PreviousDashboardRowViewModel> applicationsPreviouslySubmitted = toViewModelForPrevious(applicantDashboardResource.getPrevious());

        return new ApplicantDashboardViewModel(applicationsInSetUp, applicationsForEuGrantTransfers, applicationsInProgress, applicationsPreviouslySubmitted, originQuery);
    }

    private List<InSetupDashboardRowViewModel> toViewModelForInSetup(List<DashboardApplicationInSetupResource> inSetupResources){
        return inSetupResources
                .stream()
                .map(application -> new InSetupDashboardRowViewModel(
                        application.getTitle(),
                        application.getApplicationId(),
                        application.getCompetitionTitle(),
                        application.getProjectId(),
                        application.getProjectTitle()))
                .sorted()
                .collect(toList());
    }

    private List<EuGrantTransferDashboardRowViewModel> toViewModelForEuGrantTransfers(List<DashboardApplicationForEuGrantTransferResource> euGrantTransferResources){
        return euGrantTransferResources
                .stream()
                .map(application -> new EuGrantTransferDashboardRowViewModel(
                        application.getTitle(),
                        application.getApplicationId(),
                        application.getCompetitionTitle(),
                        application.getApplicationState(),
                        application.getApplicationProgress(),
                        application.getProjectId()))
                .sorted()
                .collect(toList());
    }

    private List<InProgressDashboardRowViewModel> toViewModelForInProgress(List<DashboardApplicationInProgressResource> dashboardApplicationInProgressResources){
        return dashboardApplicationInProgressResources
                .stream()
                .map(dashboardApplicationInProgressResource -> new InProgressDashboardRowViewModel(
                        dashboardApplicationInProgressResource.getTitle(),
                        dashboardApplicationInProgressResource.getApplicationId(),
                        dashboardApplicationInProgressResource.getCompetitionTitle(),
                        dashboardApplicationInProgressResource.isAssignedToMe(),
                        dashboardApplicationInProgressResource.getApplicationState(),
                        dashboardApplicationInProgressResource.isLeadApplicant(),
                        dashboardApplicationInProgressResource.getEndDate(),
                        dashboardApplicationInProgressResource.getDaysLeft(),
                        dashboardApplicationInProgressResource.getApplicationProgress(),
                        dashboardApplicationInProgressResource.isAssignedToInterview()))
                .sorted()
                .collect(toList());
    }

    private List<PreviousDashboardRowViewModel> toViewModelForPrevious(List<DashboardPreviousApplicationResource> applicantDashboardResource){
        return applicantDashboardResource
                .stream()
                .map(dashboardPreviousApplicationResource -> new PreviousDashboardRowViewModel(
                        dashboardPreviousApplicationResource.getTitle(),
                        dashboardPreviousApplicationResource.getApplicationId(),
                        dashboardPreviousApplicationResource.getCompetitionTitle(),
                        dashboardPreviousApplicationResource.getApplicationState()))
                .sorted()
                .collect(toList());
    }

}