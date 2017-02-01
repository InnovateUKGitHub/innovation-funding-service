package org.innovateuk.ifs.management.viewmodel;

/**
 * Base view model for Competition Management Applications table rows.
 */
abstract class BaseApplicationsRowViewModel {

    private long applicationNumber;
    private String projectTitle;
    private String lead;

    BaseApplicationsRowViewModel(long applicationNumber, String projectTitle, String lead) {
        this.applicationNumber = applicationNumber;
        this.projectTitle = projectTitle;
        this.lead = lead;
    }

    public long getApplicationNumber() {
        return applicationNumber;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getLead() {
        return lead;
    }
}
