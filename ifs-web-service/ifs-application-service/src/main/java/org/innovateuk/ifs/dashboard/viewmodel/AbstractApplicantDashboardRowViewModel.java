package org.innovateuk.ifs.dashboard.viewmodel;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Applicant dashboard row view model
 */
public abstract class AbstractApplicantDashboardRowViewModel<T extends AbstractApplicantDashboardRowViewModel> implements Comparable<T> {

    private final String title;
    private final long applicationNumber;
    private final String competitionTitle;

    public AbstractApplicantDashboardRowViewModel(String title, long applicationNumber, String competitionTitle) {
        this.title = title;
        this.applicationNumber = applicationNumber;
        this.competitionTitle = competitionTitle;
    }

    public String getTitle() {
        return isNullOrEmpty(title) ? "Untitled application (start here)" : title;
    }

    public long getApplicationNumber() {
        return applicationNumber;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    /* View logic */
    public abstract String getLinkUrl();

}
