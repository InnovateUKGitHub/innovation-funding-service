package org.innovateuk.ifs.applicant.resource.dashboard;

import java.time.LocalDate;

import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.IN_SETUP;

/**
 * Resource representing a project for use in the setup section of the applicant dashboard.
 */
public class DashboardInSetupRowResource extends DashboardRowResource {

    private long projectId;
    private String projectTitle;
    private LocalDate targetStartDate;
    private boolean pendingPartner;
    private long organisationId;

    // Private constructor to enforce immutability
    private DashboardInSetupRowResource() {
        this.dashboardSection = IN_SETUP;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public LocalDate getTargetStartDate() {
        return targetStartDate;
    }

    public boolean isPendingPartner() {
        return pendingPartner;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public static final class DashboardInSetupRowResourceBuilder {
        protected String title;
        protected long applicationId;
        protected String competitionTitle;
        protected DashboardSection dashboardSection;
        private long projectId;
        private String projectTitle;
        private LocalDate targetStartDate;
        private boolean pendingPartner;
        private long organisationId;

        private DashboardInSetupRowResourceBuilder() {
        }

        public static DashboardInSetupRowResourceBuilder aDashboardInSetupRowResource() {
            return new DashboardInSetupRowResourceBuilder();
        }

        public DashboardInSetupRowResourceBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public DashboardInSetupRowResourceBuilder withApplicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public DashboardInSetupRowResourceBuilder withProjectId(long projectId) {
            this.projectId = projectId;
            return this;
        }

        public DashboardInSetupRowResourceBuilder withCompetitionTitle(String competitionTitle) {
            this.competitionTitle = competitionTitle;
            return this;
        }

        public DashboardInSetupRowResourceBuilder withProjectTitle(String projectTitle) {
            this.projectTitle = projectTitle;
            return this;
        }

        public DashboardInSetupRowResourceBuilder withDashboardSection(DashboardSection dashboardSection) {
            this.dashboardSection = dashboardSection;
            return this;
        }

        public DashboardInSetupRowResourceBuilder withTargetStartDate(LocalDate targetStartDate) {
            this.targetStartDate = targetStartDate;
            return this;
        }

        public DashboardInSetupRowResourceBuilder withPendingPartner(boolean pendingPartner) {
            this.pendingPartner = pendingPartner;
            return this;
        }

        public DashboardInSetupRowResourceBuilder withOrganisationId(long organisationId) {
            this.organisationId = organisationId;
            return this;
        }

        public DashboardInSetupRowResource build() {
            DashboardInSetupRowResource dashboardInSetupRowResource = new DashboardInSetupRowResource();
            dashboardInSetupRowResource.projectTitle = this.projectTitle;
            dashboardInSetupRowResource.applicationId = this.applicationId;
            dashboardInSetupRowResource.dashboardSection = this.dashboardSection;
            dashboardInSetupRowResource.competitionTitle = this.competitionTitle;
            dashboardInSetupRowResource.targetStartDate = this.targetStartDate;
            dashboardInSetupRowResource.title = this.title;
            dashboardInSetupRowResource.organisationId = this.organisationId;
            dashboardInSetupRowResource.projectId = this.projectId;
            dashboardInSetupRowResource.pendingPartner = this.pendingPartner;
            return dashboardInSetupRowResource;
        }
    }
}