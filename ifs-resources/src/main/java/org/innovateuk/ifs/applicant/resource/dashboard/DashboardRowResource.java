package org.innovateuk.ifs.applicant.resource.dashboard;

import javax.validation.constraints.NotNull;

/**
 * Abstract resource representing an application or project on the applicant dashboard
 */
public abstract class DashboardRowResource implements Comparable<DashboardRowResource> {

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
    public int compareTo(@NotNull DashboardRowResource that) {
        return this.getTitle().compareTo(that.getTitle());
    }

}