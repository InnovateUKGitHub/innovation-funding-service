package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.applicant.resource.dashboard.*;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.dashboard.viewmodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.toList;

/**
 * Populator for the applicant dashboard, it populates an {@link org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel}
 */
@Service
public class ApplicantDashboardPopulator {

    @Autowired
    private ApplicantRestService applicantRestService;

    public ApplicantDashboardViewModel populate(Long userId) {
        ApplicantDashboardResource applicantDashboardResource = applicantRestService.getApplicantDashboard(userId);
        return getApplicantDashboardViewModel(applicantDashboardResource);
    }

    private ApplicantDashboardViewModel getApplicantDashboardViewModel(ApplicantDashboardResource applicantDashboardResource) {
        List<InSetupDashboardRowViewModel> applicationsInSetUp = getViewModelForInSetup(applicantDashboardResource.getInSetup());
        List<EuGrantTransferDashboardRowViewModel> applicationsForEuGrantTransfers = getViewModelForEuGrantTransfers(applicantDashboardResource.getEuGrantTransfer());
        List<InProgressDashboardRowViewModel> applicationsInProgress = getViewModelForInProgress(applicantDashboardResource.getInProgress());
        List<PreviousDashboardRowViewModel> applicationsPreviouslySubmitted = getViewModelForPrevious(applicantDashboardResource.getPrevious());

        return new ApplicantDashboardViewModel(applicationsInSetUp, applicationsForEuGrantTransfers, applicationsInProgress, applicationsPreviouslySubmitted);
    }

    private List<InSetupDashboardRowViewModel> getViewModelForInSetup(List<DashboardInSetupRowResource> inSetupResources){
        return inSetupResources
                .stream()
                .map(InSetupDashboardRowViewModel::new)
                .sorted(comparing(InSetupDashboardRowViewModel::getTargetStartDate, nullsLast(reverseOrder())))
                .collect(toList());
    }

    private List<EuGrantTransferDashboardRowViewModel> getViewModelForEuGrantTransfers(List<DashboardEuGrantTransferRowResource> euGrantTransferResources){
        return euGrantTransferResources
                .stream()
                .map(EuGrantTransferDashboardRowViewModel::new)
                .sorted()
                .collect(toList());
    }

    private List<InProgressDashboardRowViewModel> getViewModelForInProgress(List<DashboardInProgressRowResource> dashboardApplicationInProgressResources){
        return dashboardApplicationInProgressResources
                .stream()
                .map(InProgressDashboardRowViewModel::new)
                .sorted(comparing(InProgressDashboardRowViewModel::getEndDate, nullsLast(naturalOrder())).thenComparing(InProgressDashboardRowViewModel::getStartDate, nullsLast(reverseOrder())))
                .collect(toList());
    }

    private List<PreviousDashboardRowViewModel> getViewModelForPrevious(List<DashboardPreviousRowResource> applicantDashboardResource){
        return applicantDashboardResource
                .stream()
                .map(PreviousDashboardRowViewModel::new)
                .sorted(comparing(PreviousDashboardRowViewModel::getStartDate, nullsLast(reverseOrder())))
                .collect(toList());
    }

}