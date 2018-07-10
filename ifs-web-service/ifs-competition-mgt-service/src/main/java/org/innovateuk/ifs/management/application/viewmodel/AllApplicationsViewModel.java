package org.innovateuk.ifs.management.application.viewmodel;

import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/***
 * View model for the Competition All Applications page.
 */
public class AllApplicationsViewModel extends BaseApplicationsViewModel<AllApplicationsRowViewModel> {

    private int totalNumberOfApplications;
    private int applicationsStarted;
    private int applicationsInProgress;
    private int applicationsSubmitted;
    private String backTitle;
    private String backURL;
    private boolean isSupport;

    public AllApplicationsViewModel(long competitionId,
                                    String competitionName,
                                    int totalNumberOfApplications,
                                    int applicationsStarted,
                                    int applicationsInProgress,
                                    int applicationsSubmitted,
                                    String sorting,
                                    String filter,
                                    List<AllApplicationsRowViewModel> applications,
                                    Pagination pagination,
                                    String backTitle,
                                    String backURL,
                                    boolean isSupport) {
        super(competitionId, competitionName, applications, pagination, sorting, filter);
        this.totalNumberOfApplications = totalNumberOfApplications;
        this.applicationsStarted = applicationsStarted;
        this.applicationsInProgress = applicationsInProgress;
        this.applicationsSubmitted = applicationsSubmitted;
        this.backTitle = backTitle;
        this.backURL = backURL;
        this.isSupport = isSupport;
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

    public String getBackURL() {
        return backURL;
    }

    public String getBackTitle() {
        return backTitle;
    }

    public boolean isSupport() {
        return isSupport;
    }
}
