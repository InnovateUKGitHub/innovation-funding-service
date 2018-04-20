package org.innovateuk.ifs.dashboard.viewmodel;

/**
 * Applicant dashboard row view model
 */
public abstract class AbstractApplicantDashboardRowViewModel<T extends AbstractApplicantDashboardRowViewModel> implements Comparable<T> {

    protected final String title;
    private final long applicationNumber;
    private final String competitionTitle;

    public AbstractApplicantDashboardRowViewModel(String title, long applicationNumber, String competitionTitle) {
        this.title = title;
        this.applicationNumber = applicationNumber;
        this.competitionTitle = competitionTitle;
    }


    public long getApplicationNumber() {
        return applicationNumber;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    /* View logic */
    public abstract String getLinkUrl();
    public abstract String getTitle();

}
