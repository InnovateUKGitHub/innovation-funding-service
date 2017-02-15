package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;

import java.util.List;

/***
 * View model for the Competition All Applications page.
 */
public class AllApplicationsViewModel extends BaseApplicationsViewModel<AllApplicationsRowViewModel> {

    private int totalNumberOfApplications;
    private int applicationsStarted;
    private int applicationsInProgress;
    private int applicationsSubmitted;

    public AllApplicationsViewModel(long competitionId,
                                    String competitionName,
                                    int totalNumberOfApplications,
                                    int applicationsStarted,
                                    int applicationsInProgress,
                                    int applicationsSubmitted,
                                    List<AllApplicationsRowViewModel> applications,
                                    PaginationViewModel pagination) {
        super(competitionId, competitionName, applications, pagination);
        this.totalNumberOfApplications = totalNumberOfApplications;
        this.applicationsStarted = applicationsStarted;
        this.applicationsInProgress = applicationsInProgress;
        this.applicationsSubmitted = applicationsSubmitted;
    }

    public int getTotalNumberOfApplications() {
        return totalNumberOfApplications;
    }

    public int getApplicationsStarted() {
        return applicationsStarted;
    }

    public int getApplicationsInProgress() {
        return applicationsInProgress;
    }

    public int getApplicationsSubmitted() {
        return applicationsSubmitted;
    }
}
