package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;

import java.util.List;

/***
 * View model for the Competition All Applications page.
 */
public class AllApplicationsViewModel {

    private long competitionId;
    private String competitionName;
    private int totalNumberOfApplications;
    private int applicationsStarted;
    private int applicationsInProgress;
    private int applicationsSubmitted;
    private List<AllApplicationsRowViewModel> applications;

    public AllApplicationsViewModel(long competitionId,
                                    String competitionName,
                                    int totalNumberOfApplications,
                                    int applicationsStarted,
                                    int applicationsInProgress,
                                    int applicationsSubmitted,
                                    List<AllApplicationsRowViewModel> applications) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.totalNumberOfApplications = totalNumberOfApplications;
        this.applicationsStarted = applicationsStarted;
        this.applicationsInProgress = applicationsInProgress;
        this.applicationsSubmitted = applicationsSubmitted;
        this.applications = applications;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
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

    public List<AllApplicationsRowViewModel> getApplications() {
        return applications;
    }
}
