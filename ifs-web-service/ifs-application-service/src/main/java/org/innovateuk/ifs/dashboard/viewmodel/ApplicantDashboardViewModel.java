package org.innovateuk.ifs.dashboard.viewmodel;

import java.util.List;

/**
 * Applicant dashboard view model
 */
public class ApplicantDashboardViewModel {

    private final List<InSetupDashboardRowViewModel> inSetup;
    private final List<EuGrantTransferDashboardRowViewModel> euGrantTransfers;
    private final List<InProgressDashboardRowViewModel> inProgress;
    private final List<PreviousDashboardRowViewModel> previous;
    private final boolean showCovidQuestionnaireLink;

    public ApplicantDashboardViewModel(List<InSetupDashboardRowViewModel> inSetup,
                                       List<EuGrantTransferDashboardRowViewModel> euGrantTransfers,
                                       List<InProgressDashboardRowViewModel> inProgress,
                                       List<PreviousDashboardRowViewModel> previous,
                                       boolean showCovidQuestionnaireLink) {
        this.inSetup = inSetup;
        this.inProgress = inProgress;
        this.euGrantTransfers = euGrantTransfers;
        this.previous = previous;
        this.showCovidQuestionnaireLink = showCovidQuestionnaireLink;
    }

    public List<InSetupDashboardRowViewModel> getProjects() {
        return inSetup;
    }

    public List<InProgressDashboardRowViewModel> getInProgress() {
        return inProgress;
    }

    public List<EuGrantTransferDashboardRowViewModel> getEuGrantTransfers() {
        return euGrantTransfers;
    }

    public List<PreviousDashboardRowViewModel> getPrevious() {
        return previous;
    }

    public boolean isShowCovidQuestionnaireLink() {
        return showCovidQuestionnaireLink;
    }

    /* View logic */
    public String getApplicationInProgressText() {
        return inProgress.size() == 1 ?
                "Application in progress" : "Applications in progress";
    }
}
