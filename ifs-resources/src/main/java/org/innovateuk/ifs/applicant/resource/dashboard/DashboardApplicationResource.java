package org.innovateuk.ifs.applicant.resource.dashboard;

import javax.validation.constraints.NotNull;

/**
 * Abstract resource representing the minimum requirements for an application in the applicant dashboard.
 */
public abstract class DashboardApplicationResource implements Comparable<DashboardApplicationResource> {

    protected String title;
    protected long applicationId;
    protected String competitionTitle;
    protected DashboardSection dashboardSection;

    public String getTitle() {
        return title;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public DashboardSection getDashboardSection() {
        return dashboardSection;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public int compareTo(@NotNull DashboardApplicationResource that) {
        return this.getTitle().compareTo(that.getTitle());
    }

}